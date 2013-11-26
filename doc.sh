export src='pure2D/src/'
export dst='doc'
export package='com.funzio.pure2D'
export wintitle='Pure2D Game Engine'
export version='ver 1.5.0'
export doctitle='<img src="pure2d_logo.png"><br>Pure2D<sup><font size="-2">TM</font></sup> Game Engine'
export header='<img src="pure2d_logo_64.png"><br><b>Pure2D Engine </b><br><font size="-1">'$version'</font>'
export bottom=''
#echo javadoc -d $dst -sourcepath $src -subpackages $package -windowtitle $wintitle -doctitle $doctitle -header $header -bottom $bottom
javadoc -d $dst -sourcepath $src -subpackages $package -windowtitle "$wintitle" -doctitle "$doctitle" -header "$header" -bottom "$bottom"