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

import org.activejpa.examples.petclinic.model.Owner;
import org.activejpa.examples.petclinic.model.Pet;
import org.activejpa.examples.petclinic.model.PetType;
import org.activejpa.examples.petclinic.model.Vet;
import org.activejpa.examples.petclinic.model.Visit;


/**
 * Mostly used as a facade for all Petclinic controllers
 *
 * @author Michael Isvy
 */
public interface ClinicService {

    public Collection<PetType> findPetTypes();

    public Owner findOwnerById(int id);

    public Pet findPetById(int id);

    public void savePet(Pet pet);

    public void saveVisit(Visit visit);

    public Collection<Vet> findVets();

    public void saveOwner(Owner owner);

    Collection<Owner> findOwnerByLastName(String lastName);

}
