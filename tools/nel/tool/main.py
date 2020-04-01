#!/usr/bin/python
import sys
import time
import os
import random
import subprocess
import logging
from shutil import copyfile, move
from watchdog.observers import Observer
from watchdog.events import PatternMatchingEventHandler

log = logging.getLogger()
log.setLevel(logging.DEBUG)

INPUT_PATH = '/data/input'
PROCESSING_PATH = '/data/processing'
OUTPUT_PATH = '/data/output'
TOOL_PATH = '/tool/NEEL_Linking.jar'
KB_PATH = '/kb'

class FileWatcherHandler(PatternMatchingEventHandler):
  def __init__(self, inpath, procpath, outpath, toolpath, kbpath):
    super(FileWatcherHandler, self).__init__(ignore_directories=True, patterns=['*'], ignore_patterns=['*.tmp'])
    self.inpath = inpath
    self.procpath = procpath
    self.outpath = outpath
    self.toolpath = toolpath
    self.kbpath = kbpath

  def process(self, src_path):
    filename = os.path.basename(src_path)
    tmpfile = os.path.join(self.procpath, filename)
    outfile = os.path.join(self.outpath, filename)
    
    log.info("Processing: {0}".format(src_path))
    subprocess.call(['java', '-jar', self.toolpath, src_path, self.kbpath, tmpfile])
    
    if os.path.exists(tmpfile):
      move(tmpfile, outfile)
      try:
        os.remove(src_path)
      except OSError:
        log.error("Can't delete input file %s" % src_path)

      log.info("Processing of %s completed, output file available at: %s" % (filename, outfile))

  def process_input_files(self):
    while True:
      files = [os.path.join(self.inpath, f) for f in os.listdir(self.inpath)]
      files = filter(os.path.isfile, files)
      files = filter(lambda f: not f.endswith('.tmp'), files)

      if len(files) == 0:
        break

      next_file = max(files, key=lambda x: os.path.getmtime(x))
      self.process(next_file)

  def on_created(self, event):
    self.process_input_files()
  
  def on_moved(self, event):
    self.process_input_files()

def make_directories():
  for folder in [INPUT_PATH, PROCESSING_PATH, OUTPUT_PATH]:
    if not os.path.exists(folder):
      os.mkdir(folder)

def setup_logger():
  handler = logging.StreamHandler(sys.stdout)
  handler.setLevel(logging.INFO)
  formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
  handler.setFormatter(formatter)
  log.addHandler(handler)

if __name__ == "__main__":
  setup_logger()
  make_directories()
  log.info("NEL tool started")
  
  event_handler = FileWatcherHandler(INPUT_PATH, PROCESSING_PATH, OUTPUT_PATH, TOOL_PATH, KB_PATH)
  event_handler.process_input_files()

  log.info("Waiting for input files into: %s" % INPUT_PATH)
  
  observer = Observer()
  observer.schedule(event_handler, INPUT_PATH, recursive=False)
  observer.start()
  
  try:
    while True:
      time.sleep(1)
  except KeyboardInterrupt:
    observer.stop()
  
  observer.join()