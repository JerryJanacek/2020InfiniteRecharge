/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.Winch.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Winch extends SubsystemBase {
  private TalonSRX motor = new TalonSRX(MAIN_MOTOR_ID);

  /**
   * Creates a new Winch.
   */
  public Winch() {

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
