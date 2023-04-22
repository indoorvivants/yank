## Yank

Very simple JVM micro-library for downloading self-contained tools from other 
ecosystems.

<!--toc:start-->
- [Yank](#yank)
  - [Tailwind CSS](#tailwind-css)
  - [D2](#d2)
<!--toc:end-->


### Tailwind CSS

https://tailwindcss.com/docs/installation

```scala mdoc
import com.indoorvivants.yank._

val downloadedPath: java.nio.file.Path = 
  Tool.bootstrap(new tools.TailwindCSS)(tools.TailwindCSS.Config(version = "3.2.7"))
```

### D2

https://d2lang.com/

```scala mdoc:nest
import com.indoorvivants.yank._

val downloadedPath: java.nio.file.Path = 
  Tool.bootstrap(new tools.D2)(tools.D2.Config(version = "0.4.1"))
```




|                | JVM  | 
| -------------- | ---  | 
| Scala 2.12  | ✅   | 
| Scala 2.13   | ✅   | 
| Scala 3    | ✅   | 


