package ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter;

import ch.dcreations.cncsimulator.cncControl.Canal.*;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CanalDataModel;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.GCodes.moveComands.GCodeMove;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.LogConfiguration;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CNCCodeExecuter implements Callable {


    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private final CNCProgramCommand cncProgramCommand;
    private final CanalDataModel canalDataModel;
    private Lock lock;

    public CNCCodeExecuter(CNCProgramCommand cncProgramCommand, CanalDataModel canalDataModel,Lock lock) {
        this.cncProgramCommand = cncProgramCommand;
        this.canalDataModel = canalDataModel;
        this.lock = lock;
    }

    @Override
    public Object call() throws Exception {
        try {
            setSpindleSpeed(cncProgramCommand.getAdditionParameters());
            setFeedRate(cncProgramCommand.getAdditionParameters());
            for (GCode gCode : cncProgramCommand.getgCodes()) {

                if (GCodeMove.class.isAssignableFrom(gCode.getClass())) {
                    bindAxis(((GCodeMove) gCode).getAxisPosition());
                }
                gCode.execute(canalDataModel.getCanalRunState());
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "RUN TIME EXEPTION");
        } finally {
            unbindAxis();
            boolean RunLineIsCompleted = true;
            return true;

    }
    }

    private void bindAxis(Position axisPosition) {
        canalDataModel.getCncAxes().get(AxisName.X).axisPositionProperty().bind(axisPosition.xProperty());
        canalDataModel.getCncAxes().get(AxisName.Y).axisPositionProperty().bind(axisPosition.yProperty());
        canalDataModel.getCncAxes().get(AxisName.Z).axisPositionProperty().bind(axisPosition.zProperty());
    }

    private void unbindAxis() {
        for (AxisName axisName : canalDataModel.getCncAxes().keySet()) {
            canalDataModel.getCncAxes().get(axisName).axisPositionProperty().unbind();
        }
    }


    private void setSpindleSpeed(Map<Character, Double> additionParameters) {
        if (additionParameters.containsKey('S'))
            canalDataModel.setCurrentSpindleSpeed((int) Math.round(additionParameters.get('S')));
    }

    private void setFeedRate(Map<Character, Double> additionParameters) {
        if (additionParameters.containsKey('F')) canalDataModel.setCurrentFeedRate(additionParameters.get('F'));
    }

}
