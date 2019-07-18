package com.exult.ProjectCisco.controller;

import com.exult.ProjectCisco.dto.ProfileDto;
import com.exult.ProjectCisco.dto.ProfileRelation;
import com.exult.ProjectCisco.model.Feature;
import com.exult.ProjectCisco.model.FeatureXde;
import com.exult.ProjectCisco.model.Profile;
import com.exult.ProjectCisco.repository.MavenRepository;
import com.exult.ProjectCisco.service.ifmDevice.Profile.ProfileService;
import com.exult.ProjectCisco.service.ifmDevice.maven.MavenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private MavenService mavenService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Profile getProfile(@PathVariable("id") Long id) {
        return profileService.findById(id);
    }

    @RequestMapping(value = "/all/", method = RequestMethod.GET)
    public List<Profile> getAllProfile() {
        return profileService.findAll();
    }

    @RequestMapping(value = "/feature/{id}", method = RequestMethod.GET)
    public Set<Feature> getFeature(@PathVariable("id") Long id) {
        return profileService.findById(id).getFeatures();
    }

    @RequestMapping(value = "/xde/{id}", method = RequestMethod.GET)
    public Set<FeatureXde> getXde(@PathVariable("id") Long id) {
        return profileService.getFeatureXde(id);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Profile> getProfiles() {
        return profileService.findAll();
    }


    @RequestMapping(value = "/relations/", method = RequestMethod.GET)
    public List<ProfileRelation> getRelations() {
           List<Profile> profiles=profileService.findAll();
           List<ProfileRelation> profileRelations = new ArrayList<ProfileRelation>();
        for (Profile profile:profiles) {
            if(profile.getParent()!=null){
                ProfileRelation profileRelation = new ProfileRelation();
                profileRelation.setChild(profile.getId());

                profileRelation.setParent(profile.getParent().getId());
                profileRelations.add(profileRelation);}
        }
        return profileRelations;
    }
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Profile postProfile(@RequestBody ProfileDto profileDto) {
        System.out.println(profileDto);
        return profileService.insertProfile(profileDto.getName(), mavenService.findMavenById(profileDto.getMavenId()));
    }
    // Update Profile
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Profile putProfile(ProfileDto profileDto) {
        return profileService.insertProfile(profileDto.getName(), mavenService.findMavenById(profileDto.getMavenId()));
    }

    // Delete Profile
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public boolean deleteProfile(ProfileDto profileDto) {
        return profileService.deleteProfile(mavenService.findMavenById(profileDto.getMavenId()).getId());
    }
}