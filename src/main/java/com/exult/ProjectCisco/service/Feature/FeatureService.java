package com.exult.ProjectCisco.service.Feature;

import com.exult.ProjectCisco.model.Feature;
import com.exult.ProjectCisco.model.Maven;

import java.util.Optional;
import java.util.Set;

public interface FeatureService {
    Set<Feature> findFeature(String x);
    boolean deleteFeature(Long id);
    Feature updateFeature(Long id, String name, Maven maven);
    Feature insertFeature(String name, Maven maven);
    Optional<Feature> findById(Long x);
}