import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionmanager.language.Language;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.logger.LoggerMode;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.subjects.special.Default;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
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
    private static Set<Context> specialContext = Sets.newHashSet(new Context("test", "1"), new Context("test", "2"));;

    @BeforeClass
    public static void initSubjects() {
        Logger.setLoggerMode(LoggerMode.DEBUG_IDE);

        try {
            Language.load();
            UserCollection.instance.setDirectory(FileSystems.getDefault().getPath("users"));
            GroupCollection.instance.setDirectory(FileSystems.getDefault().getPath("groups"));

            GroupCollection.instance.createDefaultGroup();

            Default.instance.getTransientSubjectData().setPermission(globalContext, "griefprevention.claim.flag.block-break", Tristate.FALSE);
            Default.instance.getSubjectData().setPermission(globalContext, "griefprevention.claim.flag.block-break.minecraft.chest", Tristate.TRUE);

            user = UserCollection.instance.createUser(UUID.randomUUID());
            groupGlobal = GroupCollection.instance.createGroup("groupGlobal");
            groupGlobal2 = GroupCollection.instance.createGroup("groupGlobal2");
            groupWorld = GroupCollection.instance.createGroup("groupWorld");
            groupWorld2 = GroupCollection.instance.createGroup("groupWorld2");

            groupGlobal.setDefaultGroup(true);

            user.getSubjectData().addParent(globalContext, groupGlobal);
            user.getSubjectData().addParent(specialContext, groupGlobal2);
            groupGlobal.getSubjectData().addParent(globalContext, groupGlobal2);
            groupGlobal2.getSubjectData().addParent(specialContext, groupWorld2);

            user.getSubjectData().addParent(worldContext, groupWorld);
            groupWorld.getSubjectData().addParent(worldContext, groupWorld2);

            user.getSubjectData().setPermission(globalContext, "perm.1", Tristate.TRUE);
            user.getSubjectData().setPermission(worldContext, "perm.2", Tristate.FALSE);
            user.getSubjectData().setPermission(specialContext, "perm.3", Tristate.TRUE);

            groupGlobal.getSubjectData().setPermission(globalContext, "perm.3", Tristate.TRUE);
            groupGlobal.getSubjectData().setPermission(worldContext, "perm.4", Tristate.FALSE);

            groupGlobal2.getSubjectData().setPermission(globalContext, "perm.5", Tristate.TRUE);
            groupGlobal2.getSubjectData().setPermission(worldContext, "perm.6", Tristate.FALSE);

            groupWorld.getSubjectData().setPermission(globalContext, "perm.7", Tristate.TRUE);
            groupWorld.getSubjectData().setPermission(worldContext, "perm.8", Tristate.FALSE);

            groupWorld2.getSubjectData().setPermission(globalContext, "perm.9", Tristate.TRUE);
            groupWorld2.getSubjectData().setPermission(worldContext, "perm.10", Tristate.FALSE);
            groupWorld2.getSubjectData().setPermission(specialContext, "perm.11", Tristate.TRUE);

            user.getSubjectData().setOption(globalContext, "option.1", "1");
            user.getSubjectData().setOption(worldContext, "option.2", "2");
            user.getSubjectData().setOption(specialContext, "option.12", "2");

            groupGlobal.getSubjectData().setOption(globalContext, "option.3", "3");
            groupGlobal.getSubjectData().setOption(worldContext, "option.4", "4");

            groupGlobal2.getSubjectData().setOption(globalContext, "option.5", "5");
            groupGlobal2.getSubjectData().setOption(worldContext, "option.6", "6");

            groupWorld.getSubjectData().setOption(globalContext, "option.7", "7");
            groupWorld.getSubjectData().setOption(worldContext, "option.8", "8");

            groupWorld2.getSubjectData().setOption(globalContext, "option.9", "9");
            groupWorld2.getSubjectData().setOption(worldContext, "option.10", "0");
            groupWorld2.getSubjectData().setOption(specialContext, "option.11", "test");

            groupGlobal.setRank(0);
            user.setLanguage(Language.getLanguage("fra"));
        } catch (SubjectIdentifierExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDefaultPermission(){
        Assert.assertEquals(true, user.getPermissionValue(globalContext, "griefprevention.claim.flag.block-break.minecraft.chest") == Tristate.TRUE);
    }

    @Test
    public void testPermissionContextsPlayerDoesntHave(){
        Assert.assertEquals(true, user.getPermissionValue(Sets.newHashSet(new Context("1", "2")), "perm.3") == Tristate.TRUE);
    }

    @Test
    public void testOptionContextsPlayerDoesntHave(){
        Assert.assertEquals(true, !user.getOption(specialContext, "perm.11").isPresent());
    }

    @Test
    public void testSpecialContextPermission(){
        Assert.assertEquals(true, user.hasPermission(specialContext, "perm.3"));
    }

    @Test
    public void testParentsSpecialContextPermission(){
        Assert.assertEquals(true, user.hasPermission(specialContext, "perm.11"));
    }

    @Test
    public void testSpecialContextOption(){
        Assert.assertEquals(true, user.getOption(specialContext, "option.12").get().equals("2"));
    }

    @Test
    public void testParentsSpecialContextOption(){
        Assert.assertEquals(true, user.getOption(specialContext, "option.11").get().equals("test"));
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
        Assert.assertEquals(true, !user.getOption(globalContext, "option.6").isPresent());
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
        Assert.assertEquals(true, user.getLanguage().equals(Language.getLanguage("fra")));
    }

    @Test
    public void checkGroupRank() {
        Assert.assertEquals(true, groupGlobal.getRank() == 0);
    }

    @Test
    public void userHasPermissionInGlobalContext() {
        Assert.assertEquals(true, user.getPermissionValue(globalContext, "perm.6") == Tristate.UNDEFINED);
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
