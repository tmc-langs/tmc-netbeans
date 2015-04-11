package fi.helsinki.cs.tmc.runners;

import fi.helsinki.cs.tmc.data.TestRunResult;
import fi.helsinki.cs.tmc.langs.RunResult;
import fi.helsinki.cs.tmc.model.TmcProjectInfo;
import fi.helsinki.cs.tmc.langs.util.TaskExecutor;
import fi.helsinki.cs.tmc.langs.util.TaskExecutorImpl;
import java.util.concurrent.Callable;

public class LangsExerciseRunner extends AbstractExerciseRunner {

    private final TaskExecutor taskExecutor = new TaskExecutorImpl();

    @Override
    public Callable<TestRunResult> getTestRunningTask(final TmcProjectInfo projectInfo) {
        return new Callable<TestRunResult>() {
            @Override
            public TestRunResult call() throws Exception {
                RunResult runResult = taskExecutor.runTests(projectInfo.getProjectDirAsPath());
                return resultParser.parseLangsResults(runResult);
            }
        };
    }
}
