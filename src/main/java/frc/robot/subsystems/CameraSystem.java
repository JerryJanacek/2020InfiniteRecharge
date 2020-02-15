/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.Objects;

public class CameraSystem extends SubsystemBase {
  private static final String SERVER_NAME = "Switched";
  private static final String WIDGET_NAME = "Driver Switching Camera";
  private ShuffleboardTab driverTab;
  private UsbCamera camera1, camera2;
  private boolean firstCamera = true;
  private MjpegServer server;
  private NetworkTableEntry nameEntry;

  /**
   * Creates a new CameraSystem.
   */
  public CameraSystem(ShuffleboardTab driverTab) {
    this.driverTab = driverTab;
    camera1 = new UsbCamera("1", 0); //todo: name them?
    camera2 = new UsbCamera("2", 1);
    setupCamera(camera1);
    setupCamera(camera2);

    nameEntry = driverTab.add("nameEntry", camera1.getName())
            .withWidget(BuiltInWidgets.kTextView)
            .withPosition(4, 0)
            .withSize(1, 1)
            .getEntry();

    // Make MjpegServer which uses dummy source
    server = CameraServer.getInstance().addSwitchedCamera(SERVER_NAME);
    // Add CameraServer widget to Shuffleboard with dummy source
    this.driverTab.add(WIDGET_NAME, server.getSource())
            .withWidget(BuiltInWidgets.kCameraStream)
            .withPosition(0,0)
            .withSize(4, 4);
    // Set source of widget to use URI of switched camera, just in case!
    NetworkTableInstance.getDefault().getTable("Shuffleboard")
            .getSubTable(driverTab.getTitle()).getSubTable(WIDGET_NAME).getEntry(".ShuffleboardURI")
            .setString("camera_server://" + SERVER_NAME);
    // Allow edit of widget to change camera
    nameEntry.addListener(notification -> {
      String value;
      if (notification.value.isString() && ((value = notification.value.getString()) != null)) {
        if (Objects.equals(lowercase(camera1.getName()), value)) {
          switchCamera(true);
        } else if (Objects.equals(lowercase(camera2.getName()), value)) {
          switchCamera(false);
        }
      }
    }, EntryListenerFlags.kImmediate | EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
  }

  private void setupCamera(UsbCamera camera) {
    camera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
  }

  public void switchCamera(boolean firstCamera) {
    UsbCamera camera = firstCamera ? camera1 : camera2;
    server.setSource(camera);
    nameEntry.setString(camera.getName());
    //driverTab.add("Driver Switching Camera", server.getSource()).withPosition(0, 0).withSize(4, 4);
  }

  public void toggleCamera() {
    switchCamera(!firstCamera);
  }

  public String lowercase(String string) {
    if (string == null) {
      return null;
    }
    return string.toLowerCase();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}