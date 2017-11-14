/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activejpa.examples.petclinic.service;

import java.util.Collection;

import javax.persistence.EntityTransaction;

import org.activejpa.examples.petclinic.model.Owner;
import org.activejpa.examples.petclinic.model.Pet;
import org.activejpa.examples.petclinic.model.PetType;
import org.activejpa.examples.petclinic.model.Vet;
import org.activejpa.examples.petclinic.model.Visit;

public class ClinicServiceImpl implements ClinicService {

    @Override
    public Collection<PetType> findPetTypes() {
    	return PetType.all();
    }

    @Override
    public Owner findOwnerById(int id) {
    	return Owner.findById(id);
    }

    @Override
    public Collection<Owner> findOwnerByLastName(String lastName) {
    	return Owner.where("lastName", lastName);
    }

    @Override
    public void saveOwner(Owner owner) {
        EntityTransaction transaction = Owner.beginTxn();
        boolean successful = false;
        try {
            owner.persist();
            successful = true;
        } finally {
            if (transaction != null) {
                if (successful) {
                    transaction.commit();
                } else {
                    transaction.rollback();
                }
            }
        }
    }

    @Override
    public void saveVisit(Visit visit) {
        visit.persist();
    }


    @Override
    public Pet findPetById(int id) {
        return Pet.findById(id);
    }

    @Override
    public void savePet(Pet pet) {
        pet.persist();
    }

    @Override
    public Collection<Vet> findVets() {
        return Vet.all();
    }

}
