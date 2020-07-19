package com.ProLabs.arstudyboard;

import com.google.ar.core.CameraConfig;
import com.google.ar.core.CameraConfigFilter;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.EnumSet;
import java.util.List;

public class CloudARFragment extends ArFragment {
    @Override
    protected Config getSessionConfiguration(Session session) {
        CameraConfigFilter filter = new CameraConfigFilter(session);
        filter.setTargetFps(EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_60));
        List<CameraConfig> cameraConfigList = session.getSupportedCameraConfigs(filter);
        if(cameraConfigList.isEmpty())
        {
            filter.setTargetFps(EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_30));
            cameraConfigList = session.getSupportedCameraConfigs(filter);
        }
        filter.setDepthSensorUsage(EnumSet.of(CameraConfig.DepthSensorUsage.DO_NOT_USE));


        session.setCameraConfig(cameraConfigList.get(0));
        Config config = session.getConfig();


        config.setCloudAnchorMode(Config.CloudAnchorMode.ENABLED);
        config.setFocusMode(Config.FocusMode.AUTO);
        session.configure(config);
        this.getArSceneView().setupSession(session);
        return config;
    }
}
