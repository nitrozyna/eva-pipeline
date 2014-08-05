import luigi
import os

from accessioning import SaveLastAccession
from shellout import shellout_no_stdout

__author__ = 'Cristina Yenyxe Gonzalez Garcia'


class VariantsLoading(luigi.Task):
    """
    Run the OpenCGA variant loading tool, whose options are (* are mandatory):

      -b, --backend
         Storage to save files into: mongo (default) or hbase (pending)
    * -c, --credentials
         Path to the file where the backend credentials are stored
          --include-effect
         Save variant effect information (optional)
         Default: false
          --include-samples
         Save samples information (optional)
         Default: false
          --include-stats
         Save statistics information (optional)
         Default: false
    * -i, --input
         Prefix of files to save in the selected backend
    """

    # TODO Possible FileParameter or PathParameter class?
    file = luigi.Parameter(description='Input VCF file to process and load')
    vcf_dir = luigi.Parameter(description='Folder for storage of EVA VCF files')
    json_dir = luigi.Parameter(description='Folder for storage of EVA JSON files')

    file_alias = luigi.Parameter(description='Unique ID that identifies the input file')
    study_alias = luigi.Parameter(description='Unique ID that identifies the study of this input file')
    study_name = luigi.Parameter(description='Full name of the study of this input file')
    study_prefix = luigi.Parameter(description='Prefix identifying the study in variant accession IDs')
    aggregated = luigi.BooleanParameter(default=False)

    def requires(self):
        return VariantsTransformation(self.file, self.vcf_dir, self.json_dir, self.file_alias, self.study_alias,
                                      self.study_name, self.study_prefix, self.aggregated)

    def run(self):
        # Get input files root name (remove .gz, then .json, then .file)
        (root_name, extension) = os.path.splitext(os.path.splitext(os.path.splitext(self.input()[0].fn)[0])[0])
        print 'Root name = ' + root_name

        # TODO --include-effect when VEP is ready
        command = '/home/cyenyxe/appl/opencga/opencga load-variants -i {input} -b mongo ' \
                  '-c /home/cyenyxe/appl/opencga/mongo.properties --include-samples --include-stats'
        kwargs = {'input': root_name}

        # Launch tool
        shellout_no_stdout(command, **kwargs)

        print "Variants loaded"

    def complete(self):
        # TODO Checking whether the loading run properly or not must be implemented
        # Intuitively, the elements in the 'variants' collection with the specified study and file ID must be equals
        # or greater than the number of lines in the JSON file. There must be an entry in the 'files' collection too.
        pass


class VariantsTransformation(luigi.Task):
    """
    Run the OpenCGA variant transformation tool, whose options are (* are mandatory):

      --aggregated
         Aggregated VCF File: basic or EVS (optional)
    * -a, --alias
         Unique ID for the file to be transformed
          --include-effect
         Save variant effect information (optional)
         Default: false
          --include-samples
         Save samples information (optional)
         Default: false
          --include-stats
         Save statistics information (optional)
         Default: false
    * -i, --input
         File to transform into the OpenCGA data model
      -o, --outdir
         Directory where output files will be saved
      -p, --pedigree
         File containing pedigree information (in PED format, optional)
    * -s, --study
         Full name of the study where the file is classified
    *     --study-alias
         Unique ID for the study where the file is classified
    """

    file = luigi.Parameter(description='Input VCF file to process and load')
    vcf_dir = luigi.Parameter(description='Folder for storage of EVA VCF files')
    json_dir = luigi.Parameter(description='Folder for storage of EVA JSON files')

    file_alias = luigi.Parameter(description='Unique ID that identifies the input file')
    study_alias = luigi.Parameter(description='Unique ID that identifies the study of this input file')
    study_name = luigi.Parameter(description='Full name of the study of this input file')
    study_prefix = luigi.Parameter(description='Prefix identifying the study in variant accession IDs')
    aggregated = luigi.BooleanParameter(default=False)

    def requires(self):
        return SaveLastAccession(self.file, self.vcf_dir, self.study_prefix)

    def run(self):
        # TODO --include-effect when VEP is ready
        command = '/home/cyenyxe/appl/opencga/opencga transform-variants -i {input} -o {outdir} ' \
                  '-a "{file-alias}" -s "{study}" --study-alias "{study-alias}" ' \
                  '--include-samples --include-stats'
        kwargs = {'input': self.input().fn,
                  'outdir': self.json_dir,
                  'file-alias': self.file_alias,
                  'study': self.study_name,
                  'study-alias': self.study_alias}

        # Fill optional arguments
        if self.aggregated:
            command += ' --aggregated basic'
            kwargs['aggregated'] = self.aggregated

        # Launch tool
        shellout_no_stdout(command, **kwargs)

    def output(self):
        input_filename = self.input().fn
        print 'Path to data model file = ' + luigi.LocalTarget(self.json_dir + os.path.basename(input_filename) + '.file.json.gz').fn
        return [luigi.LocalTarget(self.json_dir + os.path.basename(input_filename) + '.file.json.gz'),
                luigi.LocalTarget(self.json_dir + os.path.basename(input_filename) + '.variants.json.gz')]


if __name__ == '__main__':
    luigi.run()
