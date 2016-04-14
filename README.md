SecRet
=================================================
    Alan Kha        akhahaha@gmail.com
-------------------------------------------------

Summary
---------------
Calculates cumulative returns for securities.

Usage
---------------
    usage: SecRet [-f <arg>] [-h] [-v]
    Calculates cumulative returns for securities.

     -f,--filename <arg>   (REQUIRED) Securities .ini file
     -h,--help
     -v,--version

### Securities .ini File
Example:

    [QQQ]
    Ticker = QQQ
    From   = 20151001
    To     = 20151209

    [SPY]
    Ticker = SPY
    From   = 20151101
    To     = 20151209

    [UCO]
    Ticker = UCO
    From   = 20151015
    To     = 20151201

Dependencies
---------------
 - [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)
 - [Yahoo! Finance API](http://financequotes-api.com/)
