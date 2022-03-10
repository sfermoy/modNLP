cp /home/gok/incoming/English/*.{hed,xml,png,jpg,jpeg,gif,PNG,JPG,JPEG,GIF} . 2>/dev/null
./indexincoming.pl
sudo rm /home/gok/incoming/English/*.{hed,xml,png,jpg,jpeg,gif,PNG,JPG,JPEG,GIF} 2>/dev/null

