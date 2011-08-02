package fi.helsinki.cs.tmc.ui;

import fi.helsinki.cs.tmc.data.Exercise;
import fi.helsinki.cs.tmc.model.LocalCourseCache;
import fi.helsinki.cs.tmc.model.ProjectMediator;
import fi.helsinki.cs.tmc.model.TmcProjectInfo;
import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectIconAnnotator;
import org.openide.util.ChangeSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;



@ServiceProvider(service = ProjectIconAnnotator.class)
public class ExerciseIconAnnotator implements ProjectIconAnnotator {

    private static final Logger log = Logger.getLogger(ExerciseIconAnnotator.class.getName());
    
    private ChangeSupport changeSupport;
    private LocalCourseCache courses;
    private ProjectMediator projectMediator;

    public ExerciseIconAnnotator() {
        this.changeSupport = new ChangeSupport(this);
        this.courses = LocalCourseCache.getInstance();
        this.projectMediator = ProjectMediator.getInstance();
    }

    @Override
    public Image annotateIcon(Project nbProject, Image origImg, boolean openedNode) {
        TmcProjectInfo project = projectMediator.wrapProject(nbProject);
        Exercise exercise = projectMediator.tryGetExerciseForProject(project, courses);
        if (exercise == null || !exercise.getCourseName().equals(courses.getCurrentCourseName())) {
            return origImg;
        }
        
        Image img;
        try {
            img = imageForExericse(exercise);
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to load exercise icon annotation", e);
            return origImg;
        }
        
        String tooltip = tooltipForExercise(exercise);
        img = ImageUtilities.assignToolTipToImage(img, tooltip);
        
        return img;
    }
    
    private Image imageForExericse(Exercise exercise) throws IOException {
        String name = imageNameForExercise(exercise);
        return ImageIO.read(getClass().getClassLoader().getResource("fi/helsinki/cs/tmc/" + name));
    }
    
    private String imageNameForExercise(Exercise exercise) {
        if (exercise.isAttempted() && exercise.isCompleted()) {
            return "smiley.gif";
        } else if (exercise.isAttempted()) {
            return "serious.gif";
        } else {
            return "frownie.gif";
        }
    }
    
    private String tooltipForExercise(Exercise exercise) {
        if (exercise.isAttempted() && exercise.isCompleted()) {
            return "Exercise submitted - all tests successful";
        } else if (exercise.isAttempted()) {
            return "Exercise submitted - all tests not completed";
        } else {
            return "Exercise not yet submitted";
        }
    }
    
    public void updateAllIcons() {
        changeSupport.fireChange();
    }
    
    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }
}