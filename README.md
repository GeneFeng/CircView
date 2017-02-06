**1. HOW TO GET CIRCVIEW AND TEST DATA**
> Download CircView application from http://github.com/GeneFeng/CircView/blob/master/CircView.jar
> Download Annotation and CircRNAs data from https://github.com/GeneFeng/CircView/tree/master/testdata
> Download RBP data from http://gb.whu.edu.cn/CircView/testdata/rbp_human.tar.gz
> Download MRE data from http://gb.whu.edu.cn/CircView/testdata/mre_human.tar.gz

**2. HOW TO MANAGE SPECIES DATA**
> **2.1.	Species Name Management**
> The CircView integrates 5 species (Human (hg19), Mouse (mm9), Zebrafish (zv9), C.elegans (ce10), Fly (dm6)) by default. The users can also add or delete species by using menu ***“Species”->”Add Species”*** or ***“Species”->”Delete Species”***.
> ![enter image description here](image/species-add.png)![enter image description here](image/species-del.png)
> 
> **2.2.	Species Data Management**
> The users can load all species data have the compatible format with the test data annotation files. The user can also clear species data for saving memory.
> ![enter image description here](image/species-load.png)![enter image description here](image/species-clear.png)

**3. HOW TO MANAGE CIRCRNAS DATA**
> **3.1.	CircRNAs Identification Software Management**
> The CircView integrates 5 CircRNAs identification software ( circRNA_finder, CIRCexplorer, CIRI, find_circ, and Mapsplice) by default. The Users can add or delete software by using menu ***“CircRNA”->”Add Tool”*** or ***“CircRNA”->”Delete Tool”***.
> ![enter image description here](image/circrna-add.png)![enter image description here](image/circrna-del.png)
> **3.2.	CircRNAs Data Management**
> CircView can load CircRNAs data directly from the output of the default 5 CircRNAs Identification softwares. The user can also import circRNAs identified by other tools with the compatible format with output file of circRNA_finder in test data.
> The user can also clear CircRNAs data for saving memory.
> ![enter image description here](image/circrna-load.png)![enter image description here](image/circrna-clear.png)

**4.	HOW TO MANAGE RBP AND MRE DATA**
> **4.1.	RBP and MRE Data Management**
> CircRNAs mainly function as sponges for the regulatory elements, such as RNA binding proteins (RBPs) and miRNA respond elements (MREs). CircView provides advanced features to display regulatory elements. This module requires the users to install MySQL locally, see **6 HOW TO INSTALL MYSQL**. Then the users can load and display the RBP data identified by starBase (http://starbase.sysu.edu.cn/), and/or the MRE data identified by TargetScan (http://targetscan.org/) with the compatible format with http://gb.whu.edu.cn/CircView/testdata/rbp_human.tar.gz. Other regulatory elements with the same format are also compatible.
> Load RBP or MRE file will create table and deposit data into MySQL database, and Clear RBP or MRE will remove data from the database. As the data are persistent, same data should not load more than once.
> ![enter image description here](image/rpb-load.png)![enter image description here](image/rbp-clear.png)

**5.	HOW TO INSTALL JAVA VIRTUAL MACHINE**
> Java Virtual Machine should be installed before running this program. Simply access http://www.java.com, download Java, and install it.

**6.	HOW TO INSTALL MYSQL**
> **6.1.	For Windows**
> 6.1.1	Download MySQL Installation file from http://gb.whu.edu.cn/CircView/MySQL/mysql_windows.tar.gz
> 6.1.2	Decompress mysql_windows.tar.gz
> 6.1.3	Double click “NDP46-KB3045557-x86-x64-AllOS-ENU.exe” to install .NET Framework.
> 6.1.4	Double click “mysql-installer-community-5.7.16.0.msi” to install MySQL. Be sure to create password “root” for user root during installation.
> **6.2.	 For Mac OS**
> 6.2.1	Download MySQL Installation file from http://gb.whu.edu.cn/CircView/MySQL/mysql-5.7.17-macos10.12-x86_64.dmg 
> 6.2.2	Double click “mysql-5.7.17-macos10.12-x86_64.dmg” to install MySQL. Be sure to create password “root” for user root during installation.

**7.	BEST PRACTISE**
> **7.1.	Basic Feature: CircRNAs Visualization**
> 7.1.1	Java Virtual Machine should be installed before running this program. See 5 HOW TO INSTALL JAVA VIRTUAL MACHINE.
> 7.1.2	Double click CircView.jar to launch the program.
> 7.1.3	Download species data from https://github.com/GeneFeng/CircView/tree/master/testdata/annotation.tar.gz
> 7.1.4	Click “Species”->”Load Data” to upload the annotation file.
> 7.1.5	Download circRNA data from https://github.com/GeneFeng/CircView/tree/master/testdata/circRNA_finder.tar.gz
> 7.1.6	Click “CircRNA”->”Load Data” to upload the circRNA files.
> 7.1.7	Click the gene transcript name on the left panel to see the image of the circRNAs.
> 7.1.8	Gene transcript can be searched by its name or its location.
> 7.1.9	CircRNAs detail information and image can be saved for further use.
> ![enter image description here](image/image1.png)
> **7.2.	Advanced Feature: RBP and MRE sites Visualization on CircRNAs**
> 7.2.1	Install MySQL firstly, see 6 HOW TO INSTALL MYSQL
> 7.2.2	Restart the CircView.jar
> 7.2.3	Download RBP data from http://gb.whu.edu.cn/CircView/testdata/rbp_human.tar.gz
> 7.2.4	Decompress rbp_human.tar.gz
> 7.2.5	Click “RBP”->”Load Data” to upload RBP file.
> 7.2.6	Download MRE data from http://gb.whu.edu.cn/CircView/testdata/mre_human.tar.gz
> 7.2.7	Decompress mre_human.tar.gz
> 7.2.8	Click “MRE”->”Load Data” to upload MRE file.
> 7.2.9	Load Species and circRNAs, just like it do in 7.1.4 and 7.1.6
> 7.2.10	Check the RBP or MRE to add RBP sites (the red triangles) or MRE sites (the blue lines) to CircRNAs
> ![enter image description here](iamge/image2.png)

