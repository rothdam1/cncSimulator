package ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter;

import ch.dcreations.cncsimulator.animation.AnimationModel;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CanalDataModel;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.GCodes.moveComands.GCodeMove;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * <p>
 * <p>
 * CNC Code Executes implements Callable to execute a CNC Code Line in a Thread
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */

public class CNCCommandExecutor implements Callable<Boolean> {


    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private final CNCCommand cncProgramCommand;
    private final CanalDataModel canalDataModel;

    private Optional<AnimationModel> animationModelOptional = Optional.empty() ;
    AtomicBoolean brakeRunningCode;
    public CNCCommandExecutor(CNCCommand cncProgramCommand, CanalDataModel canalDataModel, AtomicBoolean brakeRunningCode) {
        this.cncProgramCommand = cncProgramCommand;
        this.canalDataModel = canalDataModel;
        this.brakeRunningCode = brakeRunningCode;
    }

    public CNCCommandExecutor(CNCCommand cncProgramCommand, CanalDataModel canalDataModel, AtomicBoolean brakeRunningCode, Optional<AnimationModel> animationModelOptional) {
        this(cncProgramCommand,canalDataModel,brakeRunningCode);
        this.animationModelOptional = animationModelOptional;
    }

    @Override
    public Boolean call(){
        try {
            setSpindleSpeed(cncProgramCommand.getAdditionParameters());
            setFeedRate(cncProgramCommand.getAdditionParameters());
            for (GCode gCode : cncProgramCommand.getGCodes()) {
                if (GCodeMove.class.isAssignableFrom(gCode.getClass())) {
                    bindAxis(((GCodeMove) gCode).getAxisPosition());
                    if (animationModelOptional.isPresent()) {
                        ((GCodeMove) gCode).setAnimationModel(animationModelOptional);
                    }
                }
                gCode.execute(canalDataModel.getCanalRunState(),brakeRunningCode);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        } finally {
            unbindAxis();
        }
        return true;
    }

    private void bindAxis(Position axisPosition) {
        canalDataModel.getCncAxes().get(AxisName.X).axisPositionProperty().bindBidirectional(axisPosition.xProperty());
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
