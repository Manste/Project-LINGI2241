set terminal png size 1024,768
set output "cpu.png"
set title "CPU usage"
set xlabel "time"
set ylabel "percent"
set xdata time
set timefmt "%d-%m %H:%M:%S"
set format x "%H:%M"
set datafile separator ','
plot "copy.csv" using 1:3 title "system" with lines, \
"copy.csv" using 1:2 title "user" with lines, \
"copy.csv" using 1:4 title "idle" with lines

set terminal png size 1024,768
set output "mem.png"
set title "Memory usage"
set xlabel "time"
set ylabel "size (MB)"
set xdata time
set timefmt "%d-%m %H:%M:%S"
set format x "%H:%M"
set datafile separator ','
plot "copy.csv" using 1:($8/1048576) title "used" with lines, \
"" using 1:($9/1048576) title "buffers" with lines, \
"" using 1:($10/1048576) title "cache" with lines, \
"" using 1:($11/1048576) title "free" with lines, \
"" using 1:($17/1048576) title "swap used" with lines, \
"" using 1:($18/1048576) title "swap free" with lines

set terminal png
set output "network.png"
set title "network"
set xlabel "time"
set ylabel "size(kB)"
set xdata time
set timefmt "%d-%m %H:%M:%S"
set format x "%H:%M"
set autoscale y
set datafile separator ','
plot "copy.csv" using 1:($12/1024) title "recv" with lines, \
"" using 1:($13/1024) title "send" with lines