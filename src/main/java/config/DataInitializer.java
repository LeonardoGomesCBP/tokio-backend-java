package config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import entities.Role;
import entities.Role.ERole;
import entities.User;
import repositories.RoleRepository;
import repositories.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        System.out.println("Inicializando dados da aplicação...");
        
        initRoles();
        
        initAdminUser();
        
        System.out.println("Inicialização de dados concluída!");
    }
    
    private void initRoles() {
        if (roleRepository.count() == 0) {
            System.out.println("Criando roles padrão...");
            
            Role userRole = new Role(ERole.ROLE_USER);
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
        } else {
            System.out.println("Roles já existem no banco de dados.");
        }
    }
    
    private void initAdminUser() {
        User existingAdmin = userRepository.findByEmail("admin@example.com");
        
        if (existingAdmin == null) {
            System.out.println("Criando usuário admin inicial...");
            
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPassword(encoder.encode("admin123"));
            
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(roles::add);
            admin.setRoles(roles);
            
            userRepository.save(admin);
        } else {
            System.out.println("Usuário admin já existe no banco de dados.");
        }
    }
} 