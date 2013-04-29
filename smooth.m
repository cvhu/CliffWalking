function out = smooth(in, N)
[m, n] = size(in);
out = zeros(m,n);
for i = 1:m
    data = in(i,:);
    f = fft(data);
    f(n/2+1-(N/2):n/2+(N/2)) = zeros(N,1);
    out(i,:) = real(ifft(f));
end
