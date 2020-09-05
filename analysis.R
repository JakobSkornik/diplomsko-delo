# install.packages("stringr", dependencies = TRUE)
# install.packages("igraph", dependencies = TRUE)
library(stringr)
library(igraph)

setwd("~/Desktop/Diploma/results")
file <- read.delim("10_16.txt")
df <- data.frame(lapply(file, as.character), stringsAsFactors=FALSE)
number_of_nodes <- as.numeric(df[1, 1])
edges <- c()
dep <- c()

# PLOTTING GRAPH

for (i in 1:number_of_nodes) {
  if (i == 1) {
    dep <- "depot"
  }
  else {
    dep <- c(dep, "not")
  }
  row <- i * 4 + 1
  neighbours_string <- df[row, 1]
  neighbours_vector <- as.numeric(strsplit(neighbours_string, " ")[[1]])
  print(neighbours_vector)
  for (j in neighbours_vector) {
    edges <- as.numeric(c(edges, c(i, j + 1)))
  }
}

edge_labels <- rep("#FFFFFFFF", length=gsize(graph1))
graph1 <- graph(edges=edges, n=number_of_nodes)
V(graph1)$depot  <- dep
E(graph1)$color <- edge_labels
colors <- rainbow(4, alpha = 1)
for (i in 1:3) {
  row <- 90 + i
  route <- df[row, 1]
  route <- as.numeric(strsplit(route, " ") [[1]])
  for (j in 1:(length(route) - 1)) {
    ei <- get.edge.ids(graph1, c(route[j] + 1, route[j + 1] + 1))
    E(graph1)[ei]$color <- colors[i]
  }
}

plot(graph1, 
     edge.arrow.size = .1,
     vertex.color = c("pink", "blue")[1 + (V(graph1)$depot == "not")],
     edge.color = E(graph1)$color)

# Averaging results

setwd("~/Desktop/Diploma/results")
results <- vector("numeric", 4)

for (j in 0:9) {
  file <- read.delim(paste("100_120_", as.character(j),".txt", sep=""))
  df <- data.frame(lapply(file, as.character), stringsAsFactors=FALSE)
  g <- as.numeric(strsplit(df[2,], ",")[[1]][2])
  g01 <- as.numeric(strsplit(df[3,], ",")[[1]][2])
  gP <- as.numeric(strsplit(df[4,], ",")[[1]][2])
  sa <- as.numeric(strsplit(df[5,], ",")[[1]][2])
  results[1] <- results[1] + g
  results[2] <- results[2] + g01
  results[3] <- results[3] + gP
  results[4] <- results[4] + sa
}
results[1] <- results[1] / 10
results[2] <- results[2] / 10
results[3] <- results[3] / 10
results[4] <- results[4] / 10

results
