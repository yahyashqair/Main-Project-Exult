package com.exult.ProjectCisco.service.ifmDevice.Device;

import com.cisco.nm.expression.function.FunctionException;
import com.exult.ProjectCisco.model.Device;

import java.util.List;

public interface DeviceService {
    Device getMatchingProfile(Device device) throws FunctionException;
    Device insertDevice(Device device) throws FunctionException;
    List<Device> getAllDevices();
    Device getDevice(Long id);
}
