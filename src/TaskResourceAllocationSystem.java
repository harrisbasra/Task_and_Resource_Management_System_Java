import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Resource {
    private final String resourceName;
    private final Map<String, String> skills;

    public Resource(String resourceName) {
        this.resourceName = resourceName;
        this.skills = new HashMap<>();
    }

    public void addSkill(String skillName, String skillLevel) {
        skills.put(skillName, skillLevel);
    }

    public boolean hasSkill(String skillName) {
        return !skills.containsKey(skillName);
    }

    public String getSkillLevel(String skillName) {
        return skills.get(skillName);
    }

    public String getResourceName() {
        return resourceName;
    }
}

class Task {
    private final String taskName;
    private final Map<String, String> requiredSkills;

    public Task(String taskName) {
        this.taskName = taskName;
        this.requiredSkills = new HashMap<>();
    }

    public void addRequiredSkill(String skillName, String skillLevel) {
        requiredSkills.put(skillName, skillLevel);
    }

    public boolean matchesResource(Resource resource) {
        for (Map.Entry<String, String> entry : requiredSkills.entrySet()) {
            String requiredSkill = entry.getKey();
            String requiredLevel = entry.getValue();
            if (resource.hasSkill(requiredSkill)) {
                return false;
            }
            String resourceLevel = resource.getSkillLevel(requiredSkill);
            if (!isSkillLevelSufficient(resourceLevel, requiredLevel)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSkillLevelSufficient(String resourceLevel, String requiredLevel) {
        // Define your custom logic for comparing skill levels here
        // For example, you can use a predefined order (e.g., beginner < intermediate < expert)
        // In this example, we assume that a higher number indicates a higher skill level
        Map<String, Integer> skillOrder = new HashMap<>();
        skillOrder.put("beginner", 1);
        skillOrder.put("intermediate", 2);
        skillOrder.put("expert", 3);

        int resourceOrder = skillOrder.getOrDefault(resourceLevel.toLowerCase(), 0);
        int requiredOrder = skillOrder.getOrDefault(requiredLevel.toLowerCase(), 0);

        return resourceOrder >= requiredOrder;
    }

    public String getTaskName() {
        return taskName;
    }

    public Set<String> getRequiredSkills() {
        return requiredSkills.keySet();
    }
}

interface MatchingStrategy {
    boolean isMatch(Resource resource, Task task);
}

class ExactMatch implements MatchingStrategy {
    @Override
    public boolean isMatch(Resource resource, Task task) {
        return task.matchesResource(resource);
    }
}

class SkillOnlyMatch implements MatchingStrategy {
    @Override
    public boolean isMatch(Resource resource, Task task) {
        for (String requiredSkill : task.getRequiredSkills()) {
            if (resource.hasSkill(requiredSkill)) {
                return false;
            }
        }
        return true;
    }
}

public class TaskResourceAllocationSystem {
    public static void main(String[] args) {
        List<Resource> resources = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        readResourceData(resources);
        readTaskData(tasks);

        MatchingStrategy exactMatch = new ExactMatch();
        MatchingStrategy skillOnlyMatch = new SkillOnlyMatch();

        allocateResourcesToTasks(resources, tasks, exactMatch);
        allocateResourcesToTasks(resources, tasks, skillOnlyMatch);
    }

    private static void readResourceData(List<Resource> resources) {
        try (BufferedReader br = new BufferedReader(new FileReader("resources.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String resourceName = parts[0].trim();
                    String[] skillData = parts[1].trim().split(",");
                    Resource resource = new Resource(resourceName);
                    for (String skillDatum : skillData) {
                        String[] skillInfo = skillDatum.trim().split(":");
                        if (skillInfo.length == 2) {
                            String skillName = skillInfo[0].trim();
                            String skillLevel = skillInfo[1].trim().toLowerCase();
                            resource.addSkill(skillName, skillLevel);
                        }
                    }
                    resources.add(resource);
                }
            }
        } catch (IOException ignored) {

        }
    }

    private static void readTaskData(List<Task> tasks) {
        try (BufferedReader br = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    Task task = getTask(parts);
                    tasks.add(task);
                }
            }
        } catch (IOException ignored) {

        }
    }

    private static Task getTask(String[] parts) {
        String taskName = parts[0].trim();
        String[] skillData = parts[1].trim().split(",");
        Task task = new Task(taskName);
        for (String skillDatum : skillData) {
            String[] skillInfo = skillDatum.trim().split(":");
            if (skillInfo.length == 2) {
                String skillName = skillInfo[0].trim();
                String skillLevel = skillInfo[1].trim().toLowerCase();
                task.addRequiredSkill(skillName, skillLevel);
            }
        }
        return task;
    }

    private static void allocateResourcesToTasks(List<Resource> resources, List<Task> tasks, MatchingStrategy strategy) {
        for (Task task : tasks) {
            System.out.println("Matching resources for task: " + task.getTaskName());
            for (Resource resource : resources) {
                if (strategy.isMatch(resource, task)) {
                    System.out.println("Resource: " + resource.getResourceName());
                }
            }
            System.out.println();
        }
    }
}
