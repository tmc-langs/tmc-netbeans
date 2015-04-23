package fi.helsinki.cs.tmc.runners;

import fi.helsinki.cs.tmc.data.TestRunResult;
import fi.helsinki.cs.tmc.langs.RunResult;
import fi.helsinki.cs.tmc.langs.SpecialLogs;
import fi.helsinki.cs.tmc.model.TmcProjectInfo;
import fi.helsinki.cs.tmc.langs.util.TaskExecutor;
import fi.helsinki.cs.tmc.langs.util.TaskExecutorImpl;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public class LangsExerciseRunner extends AbstractExerciseRunner {

    private final TaskExecutor taskExecutor = new TaskExecutorImpl();
    private static final Logger log = Logger.getLogger(LangsExerciseRunner.class.getName());

    @Override
    public Callable<TestRunResult> getTestRunningTask(final TmcProjectInfo projectInfo) {
        return new Callable<TestRunResult>() {
            @Override
            public TestRunResult call() throws Exception {
                RunResult runResult = taskExecutor.runTests(projectInfo.getProjectDirAsPath());
                printNetbeansOutput(projectInfo, runResult);
                return resultParser.parseLangsResults(runResult);
            }
        };
    }

    private void printNetbeansOutput(TmcProjectInfo projectInfo, RunResult runResult) {
        InputOutput io = IOProvider.getDefault().getIO(projectInfo.getProjectName(), false);
        byte[] compilerOut = runResult.logs.get(SpecialLogs.COMPILER_OUTPUT);
        byte[] stdout = runResult.logs.get(SpecialLogs.STDOUT);
        byte[] stderr = runResult.logs.get(SpecialLogs.STDERR);
        if (compilerOut != null) {
            io.getOut().print(Arrays.toString(compilerOut));
        }
        if (stdout != null) {
            io.getOut().print(Arrays.toString(stdout));
        }
        if (stderr != null) {
            io.getErr().print(Arrays.toString(stderr));
        }
    }
}
