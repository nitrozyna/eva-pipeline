/*
 * Copyright 2016 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.eva.pipeline.parameters.validation.step;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

import uk.ac.ebi.eva.pipeline.parameters.JobParametersNames;
import uk.ac.ebi.eva.test.rules.PipelineTemporaryFolderRule;
import uk.ac.ebi.eva.test.utils.TestFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.fail;

/**
 * Tests that the arguments necessary to run a {@link uk.ac.ebi.eva.pipeline.jobs.steps.PopulationStatisticsLoaderStep}
 * are correctly validated
 */
public class VariantLoaderStepParametersValidatorTest {

    private JobParametersValidator validator;

    @Rule
    public PipelineTemporaryFolderRule temporaryFolderRule = new PipelineTemporaryFolderRule();

    @Before
    public void setUp() {
        validator = new VariantLoaderStepParametersValidator();
    }

    @Test
    public void allJobParametersAreValid() throws JobParametersInvalidException, IOException {
        final File dir = TestFileUtils.getResource("/parameters-validation/");
        final File inputVcf = TestFileUtils.getResource("/parameters-validation/file.vcf.gz");

        dir.setReadable(true);
        dir.setWritable(true);
        inputVcf.setReadable(true);

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                .addString(JobParametersNames.DB_NAME, "database")
                .addString(JobParametersNames.DB_COLLECTIONS_VARIANTS_NAME, "variants")
                .addString(JobParametersNames.DB_COLLECTIONS_FILES_NAME, "files")
                .addString(JobParametersNames.INPUT_STUDY_ID, "inputStudyId")
                .addString(JobParametersNames.INPUT_VCF_ID, "inputVcfId")
                .addString(JobParametersNames.INPUT_VCF, inputVcf.getCanonicalPath());

        validator.validate(jobParametersBuilder.toJobParameters());
    }

    @Test
    public void allJobParametersIncludingOptionalAreValid() throws JobParametersInvalidException, IOException {
        final File dir = TestFileUtils.getResource("/parameters-validation/");
        final File inputVcf = TestFileUtils.getResource("/parameters-validation/file.vcf.gz");

        dir.setReadable(true);
        dir.setWritable(true);
        inputVcf.setReadable(true);

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                .addString(JobParametersNames.DB_NAME, "database")
                .addString(JobParametersNames.DB_COLLECTIONS_VARIANTS_NAME, "variants")
                .addString(JobParametersNames.DB_COLLECTIONS_FILES_NAME, "files")
                .addString(JobParametersNames.INPUT_STUDY_ID, "inputStudyId")
                .addString(JobParametersNames.INPUT_VCF_ID, "inputVcfId")
                .addString(JobParametersNames.INPUT_VCF, inputVcf.getCanonicalPath())
                .addString(JobParametersNames.INPUT_VCF_AGGREGATION, "NONE")
                .addString(JobParametersNames.CONFIG_CHUNK_SIZE, "100");

        validator.validate(jobParametersBuilder.toJobParameters());
    }


}
