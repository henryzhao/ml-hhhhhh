logL <- function(theta,dat){
	alpha <- theta[1]
	beta <- theta[2]
	logL <- n*alpha*log(beta)-n*log(gamma(alpha))+(alpha-1)*sum(log(dat))-beta*sum(dat)
	return(-logL)
}
n<-length(dat)
# optim(c(2,1),logL,dat=dat)

f <- function(x, dat) {
    a <- x[1]
    b <- x[2]
    return(-sum(a*log(b)-log(gamma(a))+(a-1)*log(dat)-b*dat))
}
