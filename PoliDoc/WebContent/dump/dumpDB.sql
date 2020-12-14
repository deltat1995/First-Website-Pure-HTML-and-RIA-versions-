CREATE SCHEMA `poliDoc` ;

CREATE TABLE `poliDoc`.`folder` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(250) NOT NULL,  
  `creationDate` DATE NOT NULL,
  `IDparent` INT,
  PRIMARY KEY (`ID`),
  CONSTRAINT `subfold_const`
  FOREIGN KEY (`IDparent`)
  REFERENCES `poliDoc`.`folder` (`ID`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION);

CREATE TABLE `poliDoc`.`document` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `creationDate` DATE NOT NULL,
  `type` VARCHAR(50) NOT NULL, 
  `summary` TEXT NOT NULL,
  `IDfolder` INT NOT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT `fold_doc`
  FOREIGN KEY (`IDfolder`)
  REFERENCES `poliDoc`.`folder` (`ID`)
  ON DELETE CASCADE
  ON UPDATE CASCADE);
  
  
  
INSERT INTO `poliDoc`.`folder` (`name`, `creationDate`, `IDparent`) VALUES ('CartellaProva', '2020-05-23', NULL);
INSERT INTO `poliDoc`.`folder` (`name`, `creationDate`, `IDparent`) VALUES ('SubCartellaProva1', '2020-05-20', 1);
INSERT INTO `poliDoc`.`folder` (`name`, `creationDate`, `IDparent`) VALUES ('SubCartellaProva2', '2020-05-21', 1);
INSERT INTO `poliDoc`.`folder` (`name`, `creationDate`, `IDparent`) VALUES ('SubCartellaProva3', '2020-05-22', 1);
INSERT INTO `poliDoc`.`document` (`name`, `creationDate`, `type`, `summary`, `IDfolder`) VALUES ('DocProva','2020-05-30', 'Word Document', '
Prefacefolder
 
1 Data Mining and Analysis 
    1.1 Data Matrix 
    1.2 Attributes 
    1.3 Data: Algebraic and Geometric View
    1.3.1 Distance and Angle 
    1.3.2 Mean and Total Variance
    1.3.3 Orthogonal Projection 
    1.3.4 Linear Independence and Dimensionality 
    1.4 Data: Probabilistic View
    1.4.1 Bivariate Random Variables 
    1.4.2 Multivariate Random Variable 
    1.4.3 Random Sample and Statistics
    1.5 Data Mining
    1.5.1 Exploratory Data Analysis
    1.5.2 Frequent Pattern Mining
    1.5.3 Clustering
    1.5.4 Classification
    1.6 Further Reading
    1.7 Exercises
    
I Data Analysis Foundations
 
2 Numeric Attributes
    2.1 Univariate Analysis
    2.1.1 Measures of Central Tendency
    2.1.2 Measures of Dispersion
    2.2 Bivariate Analysis
    2.2.1 Measures of Location and Dispersion 
    2.2.2 Measures of Association
    2.3 Multivariate Analysis
    2.4 Data Normalization
    2.5 Normal Distribution
    2.5.1 Univariate Normal Distribution
    2.5.2 Multivariate Normal Distribution
    2.6 Further Reading
    2.7 Exercises
    
3 Categorical Attributes
    3.1 Univariate Analysis
    3.1.1 Bernoulli Variable
    3.1.2 Multivariate Bernoulli Variable
    3.2 Bivariate Analysis
    3.2.1 Attribute Dependence: Contingency Analysis
    3.3 Multivariate Analysis
    3.3.1 Multi-way Contingency Analysis
    3.4 Distance and Angle
    3.5 Discretization
    3.6 Further Reading
    3.7 Exercises

4 Graph Data
    4.1 Graph Concepts
    4.2 Topological Attributes
    4.3 Centrality Analysis
    4.3.1 Basic Centralities
    4.3.2 Web Centralities
    4.4 Graph Models
    4.4.1 Erdös-Rényi Random Graph Model
    4.4.2 Watts-Strogatz Small-world Graph Model
    4.4.3 Barabási-Albert Scale-free Model
    4.5 Further Reading
    4.6 Exercises', 2);
