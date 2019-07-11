package com.exult.ProjectCisco.service.deviceLoader;

import com.exult.ProjectCisco.model.*;
import com.exult.ProjectCisco.repository.FeatureRepository;
import com.exult.ProjectCisco.repository.FeatureXdeRepository;
import com.exult.ProjectCisco.repository.MavenRepository;
import com.exult.ProjectCisco.repository.ProfileRepository;
import com.exult.ProjectCisco.service.ifmDevice.Feature.FeatureService;
import com.exult.ProjectCisco.service.ifmDevice.Xde.XdeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class Loader {

    @Autowired
    XdeService xdeService;

    @Autowired
    FeatureService featureService;

    @Autowired
    FeatureXdeRepository featureXdeRepository;

    @Autowired
    FeatureRepository featureRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    MavenRepository mavenRepository;


    // Store all XmpXde and XmpFeature here For Sorting xml and feature to avoid errors
    ArrayList<File> xdeFiles = new ArrayList<>();
    ArrayList<File> featureFiles = new ArrayList<>();
    ArrayList<File> profileFiles = new ArrayList<>();
    // Array for Solve the dependency between profiles
    HashMap<String, String> profileMap = new HashMap<String, String>();
    /*
     * Helper function for parse the xml pages and convert it into DOM object
     * */
    Document parse(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(file);
        document.getDocumentElement().normalize();
        return document;
    }

    /*
     *   Take file directory and run the loader
     * */
    public void run() throws IOException, ParserConfigurationException, SAXException {
        File folder = new File("C:\\Users\\user\\Desktop\\test2");
        listAllFiles(folder);
        storeInOrder();
    }

    // Recurrence function that open all folders and explore files
    public void listAllFiles(File folder) throws IOException, ParserConfigurationException, SAXException {
        File[] fileNames = folder.listFiles();
        for (File file : fileNames) {
            // if directory call the same method again
            if (file.isDirectory()) {
                listAllFiles(file);
            } else {
                if (file.getName().equals("xmpxde.xml")) {
                    readXde(file);
                } else if (file.getName().equals("xmpfeature.xml")) {
                    featureFiles.add(file);
                }else if (file.getName().equals("xmpdevice.xml")){
                    profileFiles.add(file);
                }
            }
        }
    }

    /*
     * Function that store Xdes first then store Features
     * */
    public void storeInOrder() throws ParserConfigurationException, SAXException, IOException {
//        for (File xdeFile: xdeFiles) {
//            readXde(xdeFile);
//        }
        for (int i = 0; i < featureFiles.size(); i++) {
            readFeature(featureFiles.get(i));
        }
        for (int i = 0; i < profileFiles.size(); i++) {
            readProfile(profileFiles.get(i));
        }
        /*
        * Solve profile Dependency !!
        * */
        for (Map.Entry<String, String> entry : profileMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            solveProfileDependency(key,value);
        }
    }

    private void solveProfileDependency(String child,String parent){
        if (parent == null)return;
        try {
            solveProfileDependency(parent, profileMap.get(parent));
            Profile p1 = profileRepository.findByName(child);
            Profile p2 = profileRepository.findByName(parent);
            Set<Feature> featureSet = p1.getFeatures();
            featureSet.addAll(p2.getFeatures());
            p1.setFeatures(featureSet);
            featureSet.removeAll(p1.getExcludeFeature());
        }catch (Exception e){
            System.err.println("Error"+e.getMessage());
        }
        profileMap.put(child,null);
    }

    // Tested
    public void readXde(File file) throws IOException, ParserConfigurationException, SAXException {
        Document document = parse(file);
        System.err.println(file.getPath());
        Element element = document.getDocumentElement();
        NodeList nodeList = element.getChildNodes();
        Maven maven = new Maven();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName() == "groupId") {
                maven.setGroupId(node.getTextContent());
            } else if (node.getNodeName() == "artifactId") {
                maven.setArtifactId(node.getTextContent());
            } else if (node.getNodeName() == "version") {
                maven.setVersion(node.getTextContent());
            }
        }
        xdeService.insertXde(maven.getGroupId() + maven.getArtifactId(), maven);
    }
    //Tested
    @Transactional
    public void readFeature(File file) throws IOException, SAXException, ParserConfigurationException {
        Document document = parse(file);
        Element element = document.getDocumentElement();
        Feature feature = new Feature();

        /*
         * Loop to extract maven information
         * */
        NodeList nodeList1 = element.getChildNodes();
        Maven maven = new Maven();
        for (int i = 0; i < nodeList1.getLength(); i++) {
            Node node = nodeList1.item(i);
            if (node.getNodeName() == "groupId") {
                maven.setGroupId(node.getTextContent());
            } else if (node.getNodeName() == "artifactId") {
                maven.setArtifactId(node.getTextContent());
            } else if (node.getNodeName() == "version") {
                maven.setVersion(node.getTextContent());
            }
        }
        feature.setMaven(maven);
        mavenRepository.save(maven);
        featureRepository.save(feature);
        //System.out.println(maven);
        /*
         * Loop to extract Dependencies ' Xde '
         * */
        NodeList nList = document.getElementsByTagName("dependency");
        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            System.out.println("namee" + node.getTextContent());
            NodeList nodeList = node.getChildNodes();
            String groupId = null, artifactId = null;
            /*
             * Find Xde Information
             * */
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node nNode = nodeList.item(j);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getNodeName().equals("groupId")) {
                        groupId = eElement.getTextContent();
                    } else if (eElement.getNodeName().equals("artifactId")) {
                        artifactId = eElement.getTextContent();
                    }
                }
            }
            Xde xde = xdeService.findXde(groupId + artifactId);
            System.out.println(xde);
            FeatureXde featureXde = new FeatureXde();
            featureXde.setFeature(feature);
            featureXde.setXde(xde);
            Set<FeatureXde> featureXdeSet = feature.getXdeSet();
            featureXde.setTypeOfRelation(findRelationType(file, xde));
            featureXdeSet.add(featureXde);
            feature.setXdeSet(featureXdeSet);
            feature.setName(maven.getGroupId() + maven.getArtifactId());
            /*
             * Find Relation type
             * */
            featureXdeRepository.save(featureXde);
        }
        featureRepository.save(feature);
    }

    public String findRelationType(File file, Xde xde) {
        try {
            String newfile = file.getParent() + "\\src\\main\\resources\\META-INF\\MANIFEST.MF";
            System.out.println(file.getPath());
            BufferedReader br = new BufferedReader(new FileReader(newfile));
            String st;
            while ((st = br.readLine()) != null) {
                if (st.contains(xde.getMaven().getGroupId() + ":" + xde.getMaven().getArtifactId())) {
                    st = br.readLine();
                    return st.substring(18);
                }
            }
        } catch (Exception e) {
            System.out.println("File Not Found " + file.getPath());
        }
        return null;
    }

    @Transactional
    public void readProfile(File file) throws IOException, SAXException, ParserConfigurationException {
        Document document = parse(file);
        Element element = document.getDocumentElement();
        Profile profile = new Profile();
        boolean hasParent = false;
        Set<Feature> featureSet = new HashSet<>();
        Set<Feature> exfeatureSet = new HashSet<>();
        /*
         * Loop to extract maven information
         * */
        NodeList nodeList1 = element.getChildNodes();
        Maven maven = new Maven();
        for (int i = 0; i < nodeList1.getLength(); i++) {
            Node node = nodeList1.item(i);
            if (node.getNodeName() == "groupId") {
                maven.setGroupId(node.getTextContent());
            } else if (node.getNodeName() == "artifactId") {
                maven.setArtifactId(node.getTextContent());
            } else if (node.getNodeName() == "version") {
                maven.setVersion(node.getTextContent());
            }
        }
        profile.setName(maven.getGroupId()+maven.getArtifactId());
        profile.setMaven(maven);
        mavenRepository.save(maven);
        profileRepository.save(profile);
        /*
         * Find Features , parent  ;
         * */
        NodeList nList = document.getElementsByTagName("dependency");
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            NodeList nodeList = node.getChildNodes();
            String groupId = null, artifactId = null, type = null;
            /*
             * Find dependency Information
             * */
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node nNode = nodeList.item(j);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getNodeName().equals("groupId")) {
                        groupId = eElement.getTextContent();
                    } else if (eElement.getNodeName().equals("artifactId")) {
                        artifactId = eElement.getTextContent();
                    } else if (eElement.getNodeName().equals("type")) {
                        type = eElement.getTextContent();
                        hasParent = true;
                    }
                }
            }
            if (type != null && type.contains("dar")) {
                profileMap.put(maven.getGroupId() + maven.getArtifactId(), groupId + artifactId);
            } else {
                Feature feature = featureService.findFeature(groupId + artifactId);
                if (feature != null) {
                    featureSet.add(feature);
                }else{
                    System.err.println("Feature Not Found");
                }
            }
        }
        profile.setFeatures(featureSet);
        /*
         * Find Excluded feature
         * */
        NodeList execludingSet = document.getElementsByTagName("exclusion");
        for (int i = 0; i < execludingSet.getLength(); i++) {
            Node node = execludingSet.item(i);
            NodeList nodeList = node.getChildNodes();
            String groupId = null, artifactId = null, type = null;
            /*
             * Find feature Information
             * */
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node nNode = nodeList.item(j);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getNodeName().equals("groupId")) {
                        groupId = eElement.getTextContent();
                    } else if (eElement.getNodeName().equals("artifactId")) {
                        artifactId = eElement.getTextContent();
                    }
                }
            }
            Feature feature = featureService.findFeature(groupId + artifactId);
            if (feature != null) {
                exfeatureSet.add(feature);
            }
        }
        profile.setExcludeFeature(exfeatureSet);
        /*
         * Save profile ,
         * */
        profileRepository.save(profile);
    }

}