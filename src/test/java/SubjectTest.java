import com.google.common.collect.Sets;
import io.github.djxy.permissionManager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionManager.language.Language;
import io.github.djxy.permissionManager.logger.Logger;
import io.github.djxy.permissionManager.logger.LoggerMode;
import io.github.djxy.permissionManager.subjects.group.Group;
import io.github.djxy.permissionManager.subjects.group.GroupCollection;
import io.github.djxy.permissionManager.subjects.user.User;
import io.github.djxy.permissionManager.subjects.user.UserCollection;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import java.nio.file.FileSystems;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Samuel on 2016-08-16.
 */
public class SubjectTest {

    private static User user;
    private static Group groupGlobal;
    private static Group groupGlobal2;
    private static Group groupWorld;
    private static Group groupWorld2;
    private static Set<Context> globalContext = SubjectData.GLOBAL_CONTEXT;
    private static Set<Context> worldContext = Sets.newHashSet(new Context(Context.WORLD_KEY, "world"));

    @BeforeClass
    public static void initSubjects() {
        Logger.setLoggerMode(LoggerMode.DEBUG_IDE);

        try {
            UserCollection.instance.setDirectory(FileSystems.getDefault().getPath("users"));
            GroupCollection.instance.setDirectory(FileSystems.getDefault().getPath("groups"));

            GroupCollection.instance.createDefaultGroup();

            user = UserCollection.instance.createUser(UUID.randomUUID());
            groupGlobal = GroupCollection.instance.createGroup("groupGlobal");
            groupGlobal2 = GroupCollection.instance.createGroup("groupGlobal2");
            groupWorld = GroupCollection.instance.createGroup("groupWorld");
            groupWorld2 = GroupCollection.instance.createGroup("groupWorld2");

            groupGlobal.setDefaultGroup(true);

            user.addParent(globalContext, groupGlobal);
            groupGlobal.addParent(globalContext, groupGlobal2);

            user.addParent(worldContext, groupWorld);
            groupWorld.addParent(worldContext, groupWorld2);

            user.setPermission(globalContext, "perm.1", Tristate.TRUE);
            user.setPermission(worldContext, "perm.2", Tristate.FALSE);

            groupGlobal.setPermission(globalContext, "perm.3", Tristate.TRUE);
            groupGlobal.setPermission(worldContext, "perm.4", Tristate.FALSE);

            groupGlobal2.setPermission(globalContext, "perm.5", Tristate.TRUE);
            groupGlobal2.setPermission(worldContext, "perm.6", Tristate.FALSE);

            groupWorld.setPermission(globalContext, "perm.7", Tristate.TRUE);
            groupWorld.setPermission(worldContext, "perm.8", Tristate.FALSE);

            groupWorld2.setPermission(globalContext, "perm.9", Tristate.TRUE);
            groupWorld2.setPermission(worldContext, "perm.10", Tristate.FALSE);

            user.setOption(globalContext, "option.1", "1");
            user.setOption(worldContext, "option.2", "2");

            groupGlobal.setOption(globalContext, "option.3", "3");
            groupGlobal.setOption(worldContext, "option.4", "4");

            groupGlobal2.setOption(globalContext, "option.5", "5");
            groupGlobal2.setOption(worldContext, "option.6", "6");

            groupWorld.setOption(globalContext, "option.7", "7");
            groupWorld.setOption(worldContext, "option.8", "8");

            groupWorld2.setOption(globalContext, "option.9", "9");
            groupWorld2.setOption(worldContext, "option.10", "0");

            groupGlobal.setRank(0);
            user.setLanguage(Language.getLanguage("French"));
        } catch (SubjectIdentifierExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLastDefaultGroupValue(){
        groupGlobal2.setDefaultGroup(true);
        groupGlobal.setDefaultGroup(true);

        Assert.assertEquals(false, groupGlobal2.isDefaultGroup());
    }

    @Test
    public void testDefaultGroup(){
        groupGlobal2.setDefaultGroup(true);

        Assert.assertEquals(groupGlobal2, GroupCollection.instance.getDefaults());
    }

    @Test
    public void checkParents(){
        Assert.assertEquals(true,
                user.getParents(globalContext).contains(groupGlobal) &&
                groupGlobal.getParents(globalContext).contains(groupGlobal2) &&
                user.getParents(worldContext).contains(groupWorld) &&
                groupWorld.getParents(worldContext).contains(groupWorld2)
        );
    }

    @Test
    public void userHasOptionInGlobalContext(){
        Assert.assertEquals(true, user.getOption(globalContext, "option.6").isPresent());
    }

    @Test
    public void userHasOptionInWorldContext(){
        Assert.assertEquals(true, user.getOption(worldContext, "option.6").isPresent());
    }

    @Test
    public void globalGroupHasOptionInGlobalContext(){
        Assert.assertEquals(true, groupGlobal.getOption(globalContext, "option.5").isPresent());
    }

    @Test
    public void globalGroupHasOptionInWorldContext(){
        Assert.assertEquals(true, groupGlobal.getOption(worldContext, "option.6").isPresent());
    }

    @Test
    public void worldGroupDoesntHaveOptionInGlobalContext(){
        Assert.assertEquals(false, groupWorld.getOption(globalContext, "option.9").isPresent());
    }

    @Test
    public void worldGroupHasOptionInWorldContext(){
        Assert.assertEquals(true, groupWorld.getOption(worldContext, "option.10").isPresent());
    }

    @Test
    public void checkUserLanguage() {
        Assert.assertEquals(true, user.getLanguage().equals(Language.getLanguage("French")));
    }

    @Test
    public void checkGroupRank() {
        Assert.assertEquals(true, groupGlobal.getRank() == 0);
    }

    //Work only if the player is in the world 'world'.
    @Test
    public void userHasPermissionInGlobalContext() {
        Assert.assertEquals(true, user.getPermissionValue(globalContext, "perm.6") != Tristate.UNDEFINED);
    }

    @Test
    public void userHasPermissionInWorldContext() {
        Assert.assertEquals(true, user.getPermissionValue(worldContext, "perm.6") != Tristate.UNDEFINED);
    }

    @Test
    public void globalGroupHasPermissionInGlobalContext() {
        Assert.assertEquals(true, groupGlobal.getPermissionValue(globalContext, "perm.5") != Tristate.UNDEFINED);
    }

    @Test
    public void globalGroupHasPermissionInWorldContext() {
        Assert.assertEquals(true, groupGlobal.getPermissionValue(worldContext, "perm.6") != Tristate.UNDEFINED);
    }

    @Test
    public void worldGroupDoesntHavePermissionInGlobalContext() {
        Assert.assertEquals(true, groupWorld.getPermissionValue(globalContext, "perm.9") == Tristate.UNDEFINED);
    }

    @Test
    public void worldGroupHasPermissionInWorldContext() {
        Assert.assertEquals(true, groupWorld.getPermissionValue(worldContext, "perm.10") != Tristate.UNDEFINED);
    }

}
