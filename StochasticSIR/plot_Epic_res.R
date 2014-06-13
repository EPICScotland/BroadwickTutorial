# plotting results from SIR simulation for Tutorial

d <- read.csv('C:\\broadwick\\StochasticSIR\\stepDetailsSIR.csv')
cols <- c("red")
step <- d$Step-(d$Step[1]-1)
par(mfrow=c(3,2))

lim <- max(max(d$No.Susceptible))
matplot(step, d$No.Susceptible, type='l', xlab="Time", ylab="Susceptibles", col='red', ylim=c(0,lim))
legend("topleft", col=cols, text.col=cols, c("rate=.5", bty="n", cex=0.7)

lim <- max(max(d$No.infectious))
matplot(step, d$No.infectious, type='l', xlab="Time", ylab="Infectious", col='red', ylim=c(0,500))
legend("topleft", col=cols, text.col=cols, c("rate=.5"), bty="n", cex=0.7)

lim <- max(max(d$No.Recovered))
matplot(step, d$No.Recovered, type='l', xlab="Time", ylab="Recovered", col='red', ylim=c(0,lim))
legend("topleft", col=cols, text.col=cols, c("rate=.5", bty="n", cex=0.7)








