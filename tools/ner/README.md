# TWITTER NLP Docker image
A docker version of the [Twitter NLP](https://github.com/aritter/twitter_nlp) tool. The tool was installed at the path `/tool`

## Standard usage
See main repository: https://github.com/aritter/twitter_nlp

## Continuous processing
Run a docker container with the following command:
```
docker run --rm -v $PWD/test:/data bigtwine-ner-tool
```

Put input files into `./test/ritter-input`. Processed files will be available into `./test/ritter-output` named as the input.
