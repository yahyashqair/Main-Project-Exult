package com.exult.ProjectCisco.service.ifmDevice.Device;

import com.exult.ProjectCisco.model.Configuration;
import com.exult.ProjectCisco.model.Criteria;
import com.exult.ProjectCisco.model.Profile;
import com.exult.ProjectCisco.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class deviceServiceImplementaion implements DeviceService {
    @Autowired
    ProfileRepository profileRepository;

    @Override
    public List<Profile> getMatchingProfile(HashMap<String, String> map) {
        List<Profile> profiles = profileRepository.findAll();
        List<Profile> matchProfile = new ArrayList<>();
        for (Profile profile : profiles) {
            Map<String, Boolean> stringBooleanMap = new HashMap<>();
            for (Criteria criteria : profile.getCriteriaSet()) {
                // LOOP OVER Configurations
                if(criteria.getOperator().equals("or")){
                    boolean b=false;
                    for (Configuration configuration :criteria.getConfigurationSet()) {
                        switch (configuration.getOperation()){
                            case "equal":
                                if(map.get(criteria.getName()).equals(configuration.getValue())){
                                 b=true;
                                }
                                break;
                            case "lessAndEqual":
                                if(Integer.valueOf(map.get(criteria.getName()))<= Integer.valueOf(configuration.getValue())){
                                    b=true;
                                }
                                break;
                            case "greater":

                                if(Integer.valueOf(map.get(criteria.getName())) > Integer.valueOf(configuration.getValue())){
                                    b=true;
                                }
                                break;
                            case  "greaterAndEqual":

                                if(Integer.valueOf(map.get(criteria.getName()))>= Integer.valueOf(configuration.getValue())){
                                    b=true;
                                }
                                break;
                        }
                    }
                    stringBooleanMap.put(criteria.getName(),b);
                }else{

                }
            }
        }
//        for (Profile profile : profiles) {
//            Map<String, Boolean> stringBooleanMap = new HashMap<>();
//            for (Configuration configuration : profile.getConfigurations()) {
//                if (map.containsKey(configuration.getName())) {
//                    if (map.get(configuration.getName()).equals(configuration.getValue())) {
//                        stringBooleanMap.put(configuration.getName(), true);
//                    } else {
//                        if (stringBooleanMap.containsKey(configuration.getName()) && stringBooleanMap.get(configuration.getName())) {
//                            ;
//                        }else{
//                            stringBooleanMap.put(configuration.getName(),false);
//                        }
//                    }
//                } else {
//                    break;
//                }
//            }
//            if(!stringBooleanMap.values().isEmpty()&&!stringBooleanMap.values().contains(false)){
//                matchProfile.add(profile);
//            }
//        }
//        if(matchProfile.isEmpty()){
//            return matchProfile;
//        }
//        for (int i = 0 ; i < matchProfile.size();i++){
//            Profile p = matchProfile.get(i).getParent();
//            while(p!=null){
//                matchProfile.remove(p);
//                p=p.getParent();
//            }
//        }
        return matchProfile;
    }
}
