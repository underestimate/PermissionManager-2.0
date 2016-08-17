package io.github.djxy.permissionManager.subjects.group;

/**
 * Created by Samuel on 2016-08-12.
 */
public interface GroupListener {

    public void onGroupDeleted(Group group);

    public void onGroupRankChange();

    public void onGroupSetDefault(Group group);

}
