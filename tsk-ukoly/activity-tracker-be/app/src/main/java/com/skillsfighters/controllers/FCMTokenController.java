package com.skillsfighters.controllers;

import com.skillsfighters.domain.DeviceGroupDTO;
import com.skillsfighters.repository.DeviceGroupRepositoryCrud;
import com.skillsfighters.security.SecurityInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/token")
public class FCMTokenController implements SecurityInfo {
    private final DeviceGroupRepositoryCrud deviceGroupRepositoryCrud;

    @Autowired
    public FCMTokenController(@NotNull final DeviceGroupRepositoryCrud deviceGroupRepositoryCrud) {
        this.deviceGroupRepositoryCrud = deviceGroupRepositoryCrud;
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ResponseEntity newToken(@RequestHeader(value = "registrationToken") final String FCMToken) {
        final Optional<DeviceGroupDTO> deviceGroupDTO = deviceGroupRepositoryCrud.findByUserId(printLoggedUserFirebaseUid());
        if (deviceGroupDTO.isPresent()) {
            if (!deviceGroupDTO.get().getFCMTokens().contains(FCMToken)) {
                deviceGroupDTO.get().getFCMTokens().add(FCMToken);
            }
            DeviceGroupDTO updatedDeviceGroup = deviceGroupRepositoryCrud.save(deviceGroupDTO.get());
            log.debug("user: {} - newFCMToken - DeviceGroup {} successfully updated", printLoggedUserFirebaseUid(), updatedDeviceGroup.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.debug("user: {} - newFCMToken - no DeviceGroup for user found",
                    printLoggedUserFirebaseUid());

            DeviceGroupDTO newDeviceGroup = new DeviceGroupDTO();
            newDeviceGroup.setFirebaseUid(printLoggedUserFirebaseUid());
            newDeviceGroup.setFCMTokens(new ArrayList<>());
            newDeviceGroup.getFCMTokens().add(FCMToken);

            newDeviceGroup = deviceGroupRepositoryCrud.save(newDeviceGroup);
            log.debug("user: {} - newFCMToken - DeviceGroup {} successfully updated", printLoggedUserFirebaseUid(), newDeviceGroup.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
