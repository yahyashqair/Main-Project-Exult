package com.exult.ProjectCisco;

import com.exult.ProjectCisco.repository.MavenRepository;
import com.exult.ProjectCisco.repository.XdeRepository;
import com.exult.ProjectCisco.service.deviceLoader.DeviceLoader;
import com.exult.ProjectCisco.service.deviceLoader.Mdfdata;
import com.exult.ProjectCisco.service.ifmDevice.Device.DeviceService;
import com.exult.ProjectCisco.service.ifmDevice.Xde.XdeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ProjectCiscoApplication implements CommandLineRunner {
    @Autowired
    private MavenRepository mavenRepository;
    @Autowired
    private XdeRepository xdeRepository;
    @Autowired
    private XdeService xdeService;
    @Autowired
    DeviceLoader loader ;

    @Autowired
    DeviceService deviceService ;

    @Autowired
    Mdfdata mdfdata ;

    public static void main(String[] args) {
        SpringApplication.run(ProjectCiscoApplication.class, args);
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
       // HashMap<String,String> map=new HashMap<String,String>();
        //map.put("productSeries","281716314");
        //map.put("software","IOS");
        //System.out.println(deviceService.getMatchingProfile(map));
        //mdfdata.getDeviceDetails("1.3.6.1.4.1.9.12.3.1.3.847");
          loader.run(new File("C:\\Users\\user\\Desktop\\devices"));
        //System.out.println(loader.findConfigurationsSet(new File("C:\\Users\\user\\Desktop\\device_packages_ifm\\ifm_device_profiles\\com.cisco.ifm.deviceprofile.cat4k_wireless\\xmpdevice.xml")));
    }

}