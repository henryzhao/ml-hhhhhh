logL <- function(theta,dat){
	alpha <- theta[1]
	beta <- theta[2]
	logL <- n*alpha*log(beta)-n*log(gamma(alpha))+(alpha-1)*sum(log(dat))-beta*sum(dat)
	return(-logL)
}
#n<-length(dat)
# optim(c(2,1),logL,dat=dat)

#f <- function(x, dat) {
#    a <- x[1]
#    b <- x[2]
#    return(-sum(a*log(b)-log(gamma(a))+(a-1)*log(dat)-b*dat))
#}
# optim(c(3, 5), f, dat=dat)

# #产生随机数
# print(rgamma(9,17.85,17.9))

# #概率密度函数图
# set.seed(1)
# x <- seq(0,10,length.out=100)
# y <- dgamma(x,17.85,17.94)
  
# plot(x,y,col="red",xlim=c(0,10),ylim=c(0,2),type='l',
#      xaxs="i", yaxs="i",ylab='density',xlab='dgamma',
#      main="The Gamma Density Distribution")

# legend("topright",legend=paste("m=",c(17.85)," sd=", c(17.94)), lwd=1, col=c("red"))

# #累积分布图
# set.seed(1)
# x<-seq(0,10,length.out=100)
# y<-pgamma(x,17.85,17.94)

# plot(x,y,col="red",xlim=c(0,10),ylim=c(0,1),type='l',
#      xaxs="i", yaxs="i",ylab='density',xlab='pgamma',
#      main="The Gamma Cumulative Distribution Function")

# legend("bottomright",legend=paste("shape=",c(2.225)," rate=", c(1.154)), lwd=1, col=c("red"))

# #ks检验
# set.seed(1)
# S<-rgamma(1000,17.85)
# ks.test(S, "pgamma", 17.85)