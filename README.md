# utilities

Contains various utility functionalities for Java projects, including:

- a simple interface to the commons-cli option parser, mostly for convenience
- different utility methods that are often needed (e.g. deleting a folder structure recursively, concatenating two arrays, ...)
- a framework to easily create modules which process inputs, generate outputs based on the inputs, and can be linked together easily
- a pipe framework, which uses threads to execute tasks in parallel, and which is able to be loaded with the modules mentioned above
- a thread-based file walker that executes a given Callable class on files that are matched by a given pattern
- a thread-based list processor, which does the same for elements of a given list instead of matched files
- many pre-implemented modules for functionalities that may be useful in various projects, like:
  - writing a list of Strings to a file
  - searching for files recursively, returning a list of the results
  - reading a file and applying a given processor method to each line
  - sequencing a list, meaning a given input list is returned element by element to another linked module
  - simple access to the thread-based file walker and list processor
  - ...