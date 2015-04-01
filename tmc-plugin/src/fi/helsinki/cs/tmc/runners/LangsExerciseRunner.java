package fi.helsinki.cs.tmc.runners;

import com.google.common.base.Optional;
import fi.helsinki.cs.tmc.data.TestRunResult;
import fi.helsinki.cs.tmc.langs.RunResult;
import fi.helsinki.cs.tmc.model.TmcProjectInfo;
import fi.helsinki.cs.tmc.langs.util.TaskExecutor;
import fi.helsinki.cs.tmc.langs.util.TaskExecutorImpl;
import java.util.concurrent.Callable;

public class LangsExerciseRunner extends AbstractExerciseRunner {

    private final TaskExecutor taskExecutor = new TaskExecutorImpl();

    @Override
    public Callable<TestRunResult> getTestRunningTask(TmcProjectInfo projectInfo) {
        final Optional<RunResult> runResult = taskExecutor.runTests(projectInfo.getProjectDirAsPath());

        return new Callable<TestRunResult>() {
            @Override
            public TestRunResult call() throws Exception {
                if (runResult.isPresent()) {
                    return resultParser.parseLangsResults(runResult.get());
                } else {
                    throw new UnsupportedOperationException("Project type not supported yet.");
                }
            }
        };
    }

}
