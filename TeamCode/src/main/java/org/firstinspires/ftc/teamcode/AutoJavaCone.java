   // 23 motor tics = 1 IN
        int motorTics;
        double yDif = ((left_drive1.getCurrentPosition() / 23) > position.get(1)) ? (left_drive1.getCurrentPosition() / 23) - position.get(1) : position.get(1) - (left_drive1.getCurrentPosition() / 23);
        double preY = left_drive1.getCurrentPosition();
        int posNeg = ((left_drive1.getCurrentPosition() / 23) > position.get(1)) ? 1 : -1;
        right_drive1.setPower(powerFactor * (posNeg));
        right_drive2.setPower(powerFactor * (posNeg));
        left_drive1.setPower(powerFactor * (posNeg));
        left_drive2.setPower(powerFactor * (posNeg));
        motorTics = left_drive1.getCurrentPosition() + (int) ((yDif * 23) * posNeg);
        if (posNeg == -1) {
            while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                telemetry.addData("pos:", left_drive1.getCurrentPosition());
                telemetry.update();
                position.set(1, position.get(1) + ((left_drive1.getCurrentPosition() - preY) / 23));
            }
        } else {
            while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                telemetry.addData("pos:", left_drive1.getCurrentPosition());
                telemetry.update();
                position.set(1, position.get(1) + ((left_drive1.getCurrentPosition() - preY) / 23));
            }
        }
        position.set(1, position.get(1) + ((left_drive1.getCurrentPosition() - preY) / 23));
        double xDif = ((left_drive1.getCurrentPosition() / 23) > position.get(0)) ? (left_drive1.getCurrentPosition() / 23) - position.get(0) : position.get(0) - (left_drive1.getCurrentPosition() / 23);
        posNeg = ((left_drive1.getCurrentPosition() / 23) > position.get(0)) ? 1 : -1;
        double preX = left_drive1.getCurrentPosition();
        right_drive1.setPower(powerFactor * (-posNeg));
        right_drive2.setPower(powerFactor * (posNeg));
        left_drive1.setPower(powerFactor * (posNeg));
        left_drive2.setPower(powerFactor * (-posNeg));
        motorTics = left_drive1.getCurrentPosition() + (int) ((xDif * 23) * posNeg);
        if (posNeg == -1) {
            while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                telemetry.addData("pos:", left_drive1.getCurrentPosition());
                telemetry.update();
                position.set(0, position.get(0) + ((left_drive1.getCurrentPosition() - preX) / 23));
            }
        } else {
            while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                telemetry.addData("pos:", left_drive1.getCurrentPosition());
                telemetry.update();
                position.set(0, position.get(0) + ((left_drive1.getCurrentPosition() - preX) / 23));
            }
        }
        position.set(0, position.get(0) + ((left_drive1.getCurrentPosition() - preX) / 23));
