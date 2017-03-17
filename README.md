**1. HOW TO GET CIRCVIEW AND TEST DATA**  
> Download CircView application from http://github.com/GeneFeng/CircView/blob/master/CircView.jar  
> Download gene annotation and circRNAs data from https://github.com/GeneFeng/CircView/blob/master/testdata   
> Download MRE data from http://gb.whu.edu.cn/CircView/testdata/mre_human.tar.gz  
> Download RBP data from http://gb.whu.edu.cn/CircView/testdata/rbp_human.tar.gz 
  
**2.	BEST PRACTISE**  
> **2.1.	Basic Feature: CircRNAs Visualization**  
> 2.1.1	Java Virtual Machine should be installed before running this program. See 6 HOW TO INSTALL JAVA VIRTUAL MACHINE.  
> 2.1.2	Double click CircView.jar to launch the program.  
> 2.1.3	Download and decompress species data from https://github.com/GeneFeng/CircView/blob/master/testdata/gene_annotation.tar.gz.  
> 2.1.4	Click “Species”->”Load Data” to load the “Human_hg19.txt” file.  
> 2.1.5	Download and decompress circRNA data from https://github.com/GeneFeng/CircView/blob/master/testdata/human.tar.gz.    
> 2.1.6	Click “CircRNA”->”Load Data” to load circRNA files according species and algorithm.  
> <img src="image/circrna-files-add.png" width="80%">  
> 2.1.7	Click gene transcript name on left panel to view the image of the circRNAs.  
> <img src="image/whole-circrna.png" width="80%">  
> 2.1.8	Gene transcript can be searched by name or location.  
> 2.1.9 Click one "Circle" to view details of each circRNA.  
> <img src="image/one-circrna.png" width="80%">  
> 2.1.10	Detailed information and image of circRNAs can be saved for further use.  
> 2.1.11	Click "Analysis"->"Comparison" to make a comparison between circRNAs with different samples and/or algorithms.  
> <img src="image/comparison-table.png" width="80%">  

>  
> **2.2.	Advanced Feature: MRE and RBP sites Visualization on CircRNAs**  
> 2.2.1	MySQL need to be installed, see 7 HOW TO INSTALL MYSQL  
> 2.2.2	Restart CircView.jar  
> 2.2.3	Download and decompress MRE data from http://gb.whu.edu.cn/CircView/testdata/mre_human.tar.gz   
> 2.2.4	Click “MRE”->”Load Data” to load MRE file.  
> 2.2.5	Download and decompress RBP data from http://gb.whu.edu.cn/CircView/testdata/rbp_human.tar.gz  
> 2.2.6	Click “RBP”->”Load Data” to load RBP file.  
> 2.2.7	Load Species and circRNAs, see 2.1.4 and 2.1.6  
> 2.2.8	Check MRE or RBP to add MRE sites (the blue lines) or RBP sites (the red triangles) to CircRNAs  
> <img src="image/circrna-mre-rbp.png" width="80%">  
  
  
**3. HOW TO MANAGE SPECIES DATA**  
> **3.1.	Species Name Management**  
> CircView provides 5 species (Human (hg19), Mouse (mm9), Zebrafish (zv9), Fly (dm6), C.elegans (ce10)) by default.  
> Users can also add or delete species by using menu “Species”->”Add Species” or “Species”->”Delete Species”.  
> <img src="image/species-add.png" width="40%">&nbsp;&nbsp;&nbsp;&nbsp;<img src="image/species-del.png" width="40%">  
>   
> **3.2.	Species Data Management**  
> Users can load gene annotation data for other species with compatible format.  
> Users can also clear species data to save memory.  
> <img src="image/species-load.png" width="40%">&nbsp;&nbsp;&nbsp;&nbsp;<img src="image/species-clear.png" width="40%">  
  
**4. HOW TO MANAGE CIRCRNAS DATA**  
> **4.1.	CircRNAs Identification Software Management**  
> CircView integrates 6 CircRNAs identification software (CIRCexplorer, circRNA_finder, CIRI, find_circ, Mapsplice, and UROBORUB) by default.  
> Users can add or delete software by using menu “CircRNA”->”Add Tool” or “CircRNA”->”Delete Tool”.  
> <img src="image/circrna-add.png" width="40%">&nbsp;&nbsp;&nbsp;&nbsp;<img src="image/circrna-del.png" width="40%">  
>   
>**4.2.	CircRNAs Data Management**  
> CircView can load CircRNAs data directly from output of default 6 CircRNAs Identification softwares.  
> Users can also import circRNAs identified by other tools with six tab delimited columns, including chromosome, start point, end point, running number/name, junction reads and strand.  
> <img src="image/circrna-load.png" width="40%"> 
  
**5.	HOW TO MANAGE MRE AND RBP DATA**   
> CircRNAs mainly function as sponges for the regulatory elements, such as miRNA respond elements (MREs) and RNA binding proteins (RBPs). CircView provides advanced features to display regulatory elements.  
> This module requires the users to install MySQL locally, see **7 HOW TO INSTALL MYSQL**. Users can load and display the MRE data identified by TargetScan (http://targetscan.org/) and/or the RBP data identified by starBase (http://starbase.sysu.edu.cn/) or any other software. The format requires five tab delimited columns, including chromosome, start site, end site, MRE/RBP name and description.  
> Load MRE or RBP file will create table and deposit data into MySQL database, and Clear MRE or RBP will remove data from the database. As the data are persistent, users should not load the same data for more than once.  
> <img src="image/mre-load.png" width="40%">&nbsp;&nbsp;&nbsp;&nbsp;<img src="image/mre-clear.png" width="40%">  
  
**6.	HOW TO INSTALL JAVA VIRTUAL MACHINE**  
> Java Virtual Machine need to be installed before running this program. Simply access http://www.java.com, download Java, and install it.  
  
**7.	HOW TO INSTALL MYSQL**  
> **7.1.	For Windows**  
> 7.1.1	Download and decompress MySQL Installation file from http://gb.whu.edu.cn/CircView/MySQL/mysql_windows.tar.gz  
> 7.1.2	Double click “NDP46-KB3045557-x86-x64-AllOS-ENU.exe” to install .NET Framework.  
> 7.1.3	Double click “mysql-installer-community-5.7.16.0.msi” to install MySQL. Please create password “12345” for user root during installation.  
>  
> **7.2.	 For Mac OS**  
> 7.2.1	Download MySQL Installation file from http://gb.whu.edu.cn/CircView/MySQL/mysql-5.7.17-macos10.12-x86_64.dmg  
> 7.2.2	Double click “mysql-5.7.17-macos10.12-x86_64.dmg” to install MySQL. Please create password “12345” for user root during installation.  
  