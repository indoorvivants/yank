## Yank

Very simple JVM micro-library for downloading self-contained tools from other 
ecosystems.

<!--toc:start-->
- [Yank](#yank)
  - [Tailwind CSS](#tailwind-css)
  - [D2](#d2)
<!--toc:end-->

Principles:

- CLI tools are downloaded to a system-defined cache location, as reported by [dirs-dev](https://github.com/dirs-dev/directories-jvm) library, but can be customised
- Tools are downloaded in a synchronous and blocking manner
- Progress reporting and formatting are fully customisable
- Minimal dependencies - only dependency at this point is dirs-dev

|                | JVM  | 
| -------------- | ---  | 
| Scala 2.12  | ✅   | 
| Scala 2.13   | ✅   | 
| Scala 3    | ✅   | 



### Tailwind CSS

https://tailwindcss.com/docs/installation

```scala mdoc
import com.indoorvivants.yank._

val downloadedPath: java.nio.file.Path = 
  tools.TailwindCSS.bootstrap(tools.TailwindCSS.Config(version = "3.2.7"))
```

### D2

https://d2lang.com/

```scala mdoc:nest
import com.indoorvivants.yank._

val downloadedPath: java.nio.file.Path = 
  tools.D2.bootstrap(tools.D2.Config(version = "0.4.1"))
```

