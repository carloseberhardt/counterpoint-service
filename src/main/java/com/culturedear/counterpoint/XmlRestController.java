/*
 * Copyright 2015 the original author or authors.
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

package com.culturedear.counterpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author James Weaver
 * @author Kenny Bastani
 * @author Josh Long
 */
@RestController
@RequestMapping("/counterpoint")
public class XmlRestController {

    private final CounterpointProperties counterpointProperties;

    @Autowired
    public XmlRestController(CounterpointProperties counterpointProperties) {
        this.counterpointProperties = counterpointProperties;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<ScorePartwise> write(@RequestBody CounterpointModel counterpointModel) {

        // prepare response XML out
        CounterpointGenerator cg = new CounterpointGenerator(
                this.counterpointProperties);
        cg.fillRhyPat();

        int[] cf = counterpointModel.getMainMelody();
        cg.setCantusFirmus(cf);

        int[] vbs = counterpointModel.getPartsInitialNotes();
        CounterpointSolution counterpointSolution = cg.anySpecies(counterpointModel.getScaleMode(),
                vbs, cf.length, counterpointModel.getCounterpointSpecies(), counterpointModel.getRulePenalties());

        return Optional.ofNullable(counterpointSolution.toScorePartwise())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));

    }
}
