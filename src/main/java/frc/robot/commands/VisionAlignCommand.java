/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import java.util.Optional;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.VisionSystem;
import frc.robot.util.limelight.Target;

public class VisionAlignCommand extends CommandBase {
  public static final double ROTATE_P = 0.1d;
  public static final double DEG_TOLERANCE = 1.0d;
  public static final double MIN_COMMAND = 0.05;
  public static final double SEEK_SPEED = 0.15;
  private DriveTrain driveTrain;
  private VisionSystem visionSystem;
  
  /**
   * Creates a new VisionAlignCommand.
   */
  public VisionAlignCommand(DriveTrain driveTrain, VisionSystem visionSystem) {
    this.driveTrain = driveTrain;
    this.visionSystem = visionSystem;
    addRequirements(driveTrain, visionSystem);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    visionSystem.setCalculateDistance(true);
    // todo : track last target to implement seek
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double steerAdjust = 0;

    Optional<Target> targetOptional = visionSystem.getLimelight().getTarget();
    if (targetOptional.isPresent()) {
      Target target = targetOptional.get();
      double xDeg = target.getX();
      double xError = -xDeg;
      if (Math.abs(xError) <= DEG_TOLERANCE) {
        return;
      }
      steerAdjust = xError * ROTATE_P;
      if (Math.abs(steerAdjust) < MIN_COMMAND) {
        steerAdjust = Math.copySign(MIN_COMMAND, steerAdjust);
      }
    } else {
      Optional<Target> lastTarget = visionSystem.getLimelight().getLastTarget();
      // Seek for target using lastTarget
      if (lastTarget.isEmpty()) {
        return;
      }
      // From signum, it would be 1 if the target is to the right, -1 if the target is to the left
      // Then we multiply that by 0.2
      steerAdjust = SEEK_SPEED * Math.signum(lastTarget.get().getX());
    }

    driveTrain.rawTankDrive(steerAdjust, -steerAdjust);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    visionSystem.setCalculateDistance(false);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
