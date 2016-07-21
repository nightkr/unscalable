import unscalable.Prelude._

n.ls().stdout.lines().runForeach(println)
n.echo("hi", "there").stdout.lines().runForeach(println)
n.echo("--help").stdout.lines().runForeach(println)

actorSystem.terminate()