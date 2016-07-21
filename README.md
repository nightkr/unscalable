(Warning: Very much a WIP, if you try to use this for anything serious right now, or ever, then you're seriously crazy and should seek help for your mental health)

# Unscalable

## What?

I recently saw [Awkward](https://github.com/iostreamer-X/Awkward). Interesting concept, but of course everything is better with types. ;)

It "takes over" the Scala REPL and adds some utilities that make working with native commands a bit more pleasant.

## How?

The easiest way to see it in action is by piping in `examples/Demo.scala` from a regular (proper) shell, like the following:
 
    $ sbt console < examples/Demo.scala

If you use SBT then you can also run it by adding the file as a "Scala script".

Or you could just fire up the REPL using `$ sbt console` and play around with it yourself...

## Huh?

Currently the main thing added is the `n` (native) object, where any method call is shelled out.

For example, calling `n.ls("-h")` is equivalent to `ls -h` in a normal shell.

The plan is to eventually also add utility methods for piping, as well as typesafe wrappers for common *NIX utilities.