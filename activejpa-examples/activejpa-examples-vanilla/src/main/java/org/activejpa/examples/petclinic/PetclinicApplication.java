/**
 * 
 */
package org.activejpa.examples.petclinic;

import java.util.Collection;

import org.activejpa.enhancer.ActiveJpaAgentLoader;
import org.activejpa.examples.petclinic.model.Owner;
import org.activejpa.examples.petclinic.model.Pet;
import org.activejpa.examples.petclinic.service.ClinicService;
import org.activejpa.examples.petclinic.service.ClinicServiceImpl;
import org.activejpa.jpa.JPA;
import org.joda.time.DateTime;

/**
 * @author ganeshs
 *
 */
public class PetclinicApplication {

    public static void main(String[] args) {
        ActiveJpaAgentLoader.instance().loadAgent();
        JPA.instance.addPersistenceUnit("petclinic");

        ClinicService service = new ClinicServiceImpl();
        
        Pet pet = new Pet();
        pet.setName("Jim");
        pet.setBirthDate(new DateTime().minusYears(1));
        service.savePet(pet);
        
        Owner owner = new Owner();
        owner.setCity("Bangalore");
        owner.setFirstName("Ganesh");
        owner.setLastName("Subramanian");
        owner.setAddress("Indiranagar");
        service.saveOwner(owner);
        
        Collection<Owner> owners = service.findOwnerByLastName(owner.getLastName());
        System.out.println("Found " + owners.size() + " owner matching the last name Subramanian");
    }
}
