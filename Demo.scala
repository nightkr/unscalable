import unscalable.Prelude._

n.ls().stdout.lines().runForeach(println)

actorSystem.terminate()