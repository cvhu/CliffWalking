setwd("/Users/cvhu/workspace/active/Stone_CS394R/hw3")
sarsa <- read.table("data/Sarsa_alpha.csv", header=F)

plot.new()

plot(1:500, sarsa[1,], type="lines")
lines(results_rand2[,2], results_rand2[,4], col="#000000", lwd=2.5)
lines(results_len[,2], results_len[,4], col='#0000ff', lwd=2.5)
lines(results_prob[,2], results_prob[,4], col='#00ff00', lwd=2.5)
lines(results_ent[,2], results_ent[,4], col='#ff0000', lwd=2.5)

legend(25000, 0.6, c("rand", "len", "prob", "ent"), lty=c(1, 1, 1, 1), lwd=c(2.5, 2.5, 2.5, 2.5), col=c("#000000", "#0000ff", "#00ff00", "#ff0000"))