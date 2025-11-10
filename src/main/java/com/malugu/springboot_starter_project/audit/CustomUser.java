package com.malugu.springboot_starter_project.audit;//package com.malugu.springboot_starter_project.audit;
//
//import java.util.Collection;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//@Data
//@EqualsAndHashCode(callSuper = false)
//public class CustomUser extends User {
//
//    /**
//     *
//     */
//    private static final long serialVersionUID = 175784484713870693L;
//
//    private Long id;
//
//    private String institutionUid;
//
//    private boolean defaultInstitution;
//
//
//    private String uid;
//
//    private String officeUid;
//
//    public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities, Long id, String uid, String institutionUid, boolean defaultInstitution, String officeUid) {
//
//        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
//        setId(id);
//        setInstitutionUid(institutionUid);
//        setDefaultInstitution(defaultInstitution);
//        setUid(uid);
//        setOfficeUid(officeUid);
//    }
//
//}
