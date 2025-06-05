package tn.esprit.spring.connectn.Repository.groups;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.connectn.Entities.groups.GroupType;
import tn.esprit.spring.connectn.Entities.groups.Group;

import java.util.List;

public interface GroupsRepository extends JpaRepository<Group, Long> {
    List<Group> findByType(GroupType type);

    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.id= :id")
    List<Group> findByMembersContaining(@Param("id") Long userId);

    @Query("SELECT g FROM Group g WHERE g.admin.id = :adminId")
    List<Group> findByAdminUserId(@Param("adminId") Long adminId);
}