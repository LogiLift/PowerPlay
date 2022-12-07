      /* Copyright (c) 2017 FIRST. All rights reserved.
       *
       * Redistribution and use in source and binary forms, with or without modification,
       * are permitted (subject to the limitations in the disclaimer below) provided that
       * the following conditions are met:
       *
       * Redistributions of source code must retain the above copyright notice, this list
       * of conditions and the following disclaimer.
       *
       * Redistributions in binary form must reproduce the above copyright notice, this
       * list of conditions and the following disclaimer in the documentation and/or
       * other materials provided with the distribution.
       *
       * Neither the name of FIRST nor the names of its contributors may be used to endorse or
       * promote products derived from this software without specific prior written permission.
       *
       * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
       * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
       * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
       * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
       * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
       * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
       * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
       * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
       * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
       * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
       * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
       */

      package org.firstinspires.ftc.teamcode;
      import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
      import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
      import com.qualcomm.robotcore.hardware.DcMotor;
      import com.qualcomm.robotcore.util.ElapsedTime;

      import java.math.BigInteger;


      /**
       * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
       * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
       * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
       * class is instantiated on the Robot Controller and executed.
       *
       * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
       * It includes all the skeletal structure that all linear OpModes contain.
       *
       * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
       * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
       */
// idk lol
      @TeleOp(name="Basic Drive", group="Linear Opmode")
//@Disabled
      public class Drive extends LinearOpMode {
          public int armDegreesToTicks(int degrees){
              return degrees * 43008;
          }
          // Declare OpMode members.
          private ElapsedTime runtime = new ElapsedTime();
          private Robot robot;
          private PIDController pid = new PIDController(.5, 0.1, 0);

          @Override
          public void runOpMode() {
              robot = new Robot(hardwareMap);

              // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
              // firstdotherobotgaming.
              //robot.initVuforia();
              // robot.initTfod();

              /**
               * Activate TensorFlow Object Detection before we wait for the start command.
               * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
               **/
              /**if (robot.tfod != null) {
               robot.tfod.activate();
               // The TensorFlow software will scale the input images from the camera to a lower resolution.
               // This can result in lower detection accuracy at longer distances (> 55cm or 22").
               // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
               // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
               // should be set to the value of the images used to create the TensorFlow Object Detection model
               // (typically 16/9).
               robot.tfod.setZoom(1.5, 16.0/9.0);
               }
               */
              telemetry.addData("Status", "Initialized");
              telemetry.update();

              // Wait for the game to start (driver presses PLAY)
              waitForStart();
              runtime.reset();

              int lastflposition = robot.frontLeft.getCurrentPosition();
              int lastfrposition = robot.frontRight.getCurrentPosition();
              int lastblposition = robot.backLeft.getCurrentPosition();
              int lastbrposition = robot.backRight.getCurrentPosition();
              int lastArmPosition = robot.armMotor.getCurrentPosition();
              BigInteger counter = new BigInteger("0");
              double armSpeed = 0;
              final double CONSTANT = 0.5;

              //  robot.armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

              // run until the end of the match (driver presses STOP)

              while (opModeIsActive()) {

                  counter.add(BigInteger.ONE);
                  double x = Math.pow(gamepad1.left_stick_x,1) * Robot.DRIVE_MULTIPLIER;
                  double y = Math.pow(gamepad1.left_stick_y,1) * Robot.DRIVE_MULTIPLIER;
                  double turn = Math.pow(gamepad1.right_stick_x,1) * Robot.DRIVE_MULTIPLIER;

                  double frontLeftSpeed = (double)(robot.frontLeft.getCurrentPosition() - lastflposition)/runtime.milliseconds();
                  double frontRightSpeed = (double)(robot.frontRight.getCurrentPosition() - lastfrposition)/runtime.milliseconds();
                  double backLeftSpeed = (double)(robot.backLeft.getCurrentPosition() - lastblposition)/runtime.milliseconds();
                  double backRightSpeed = (double)(robot.backRight.getCurrentPosition() - lastbrposition)/runtime.milliseconds();
                  runtime.reset();

                  lastflposition = robot.frontLeft.getCurrentPosition();
                  lastfrposition = robot.frontRight.getCurrentPosition();
                  lastblposition = robot.backLeft.getCurrentPosition();
                  lastbrposition = robot.backRight.getCurrentPosition();
                    /* if (!robot.armMotor.isBusy()){
                        robot.armMotor.setPower(pid.update(error));
                    } else{
                        robot.armMotor.setPower(0.0);
                    } */

                  // Tank Mode uses one stick to control each wheel.
                  // - This requires no math, but it is hard to drive forward slowly and keep straight.
                  // leftPower  = -gamepad1.left_stick_y ;
                  // rightPower = -gamepad1.right_stick_y ;

                  double triggerMultiplier = 1.0 - (gamepad1.right_trigger * 0.75);

                  // Send calculated power to wheels
                  robot.mecanumDrive(x * triggerMultiplier, y * triggerMultiplier, turn * triggerMultiplier);
                  double error = robot.armMotor.getCurrentPosition() - lastArmPosition;
                  if (!(gamepad1.right_bumper || gamepad1.left_bumper)) {
                      armSpeed = pid.update(error);
                  } else {
                      armSpeed = (0 + (gamepad1.left_bumper ? 0.1 : 0) + (gamepad1.right_bumper ? 0.1 : 0));
                  }
                  // robot.armMotor.setPower(armSpeed);
                  if (gamepad1.a) {
                      robot.armMotor.setPower(CONSTANT);
                      robot.armMotor.setTargetPosition(200);
                  }
                  if (gamepad1.b) {
                      robot.armMotor.setPower(CONSTANT);
                      robot.armMotor.setTargetPosition(800);
                  }
                  if (gamepad1.y) {
                      robot.armMotor.setPower(CONSTANT);
                      robot.armMotor.setTargetPosition(700);
                  }
                  if (gamepad1.x) {
                      robot.armMotor.setPower(CONSTANT);
                      robot.armMotor.setTargetPosition(950);
                  }

                  telemetry.addData("Target Position: ", robot.armMotor.getTargetPosition());
                  telemetry.addData("encode: ", robot.armMotor.getCurrentPosition());
                  telemetry.addData("arn speed: ", robot.armMotor.getPower());
                  lastArmPosition = robot.armMotor.getCurrentPosition();


                  telemetry.update();
          /*  if (robot.tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = robot.tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());
                    // step through the list of recognitions and display boundary info.+
                    int i = 0;
                    for (Recognition recognition : updatedRecognitions) {
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                        i++;
                    }
                    telemetry.update();
                }
            }*/
              }
          }
      }