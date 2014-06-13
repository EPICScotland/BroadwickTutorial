# plotting posterior from SIR ABC demo for Tutorial

data <- c("PUT SAMPLED BETA VALUES FROM POSTERIOR.OUT HERE")
hist(data, breaks=seq(3.0E-4,8.0E-4,l=20),freq=FALSE,col="orange",main="Posterior for total infectious", xlab="Beta",ylab="f(Beta)")
line(density(data)) # returns the density data
d <- density(data) # returns the density data
abline(v="5.0E-4")
