package fi.helsinki.cs.tmc.runners;

import com.google.common.base.Optional;
import fi.helsinki.cs.tmc.data.ResultCollector;
import fi.helsinki.cs.tmc.langs.util.TaskExecutor;
import fi.helsinki.cs.tmc.langs.util.TaskExecutorImpl;
import fi.helsinki.cs.tmc.model.ProjectMediator;
import fi.helsinki.cs.tmc.model.TmcProjectInfo;
import fi.helsinki.cs.tmc.model.TmcSettings;
import fi.helsinki.cs.tmc.stylerunner.validation.CheckstyleResult;
import fi.helsinki.cs.tmc.stylerunner.validation.ValidationResult;
import fi.helsinki.cs.tmc.ui.ConvenientDialogDisplayer;
import fi.helsinki.cs.tmc.utilities.BgTask;
import fi.helsinki.cs.tmc.utilities.BgTaskListener;

import java.util.Locale;

import org.netbeans.api.project.Project;

import org.openide.util.Exceptions;

public final class CheckstyleRunHandler implements Runnable {

    private Project project;
    private final ConvenientDialogDisplayer dialogDisplayer = ConvenientDialogDisplayer.getDefault();
    private ValidationResult validationResult = new CheckstyleResult();
    private final TaskExecutor exec = new TaskExecutorImpl();

    public void performAction(final ResultCollector resultCollector, final Project project) {

        this.project = project;

        BgTask.start("Running validations", this, new BgTaskListener<Object>() {

            @Override
            public void bgTaskFailed(final Throwable exception) {
                Exceptions.printStackTrace(exception);
                dialogDisplayer.displayError("Failed to validate the code.");

            }

            @Override
            public void bgTaskCancelled() {
            }

            @Override
            public void bgTaskReady(final Object nothing) {

                resultCollector.setValidationResult(validationResult);
            }
        });
    }

    @Override
    public void run() {

        final TmcProjectInfo projectInfo = ProjectMediator.getInstance().wrapProject(project);

        // Save all files
        ProjectMediator.getInstance().saveAllFiles();

        final Locale locale = TmcSettings.getDefault().getErrorMsgLocale();
        Optional<ValidationResult> result = exec.runCheckCodeStyle(projectInfo.getProjectDirAsPath());
        if (result.isPresent()) {
            validationResult = result.get();
        } else {
            ConvenientDialogDisplayer.getDefault().displayError("Checkstyle audit failed.");

        }
    }
}
