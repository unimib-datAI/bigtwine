# NEL Tool

## Standard
Place the input file into `./test/input/test.txt`

```
docker run --rm -v $PWD/test:/data bigtwine-nel-tool java -jar NEEL_Linking.jar /data/input/test.txt /kb /data/output/out.tsv
```

## Continuous processing
Run a docker container with the following command:
```
docker run --rm -v $PWD/test:/data bigtwine-nel-tool
```

Put input files into `./test/input`. Processed files will be available into `./test/output` named as the input.
