package com.jena.demo.service;

import com.jena.demo.config.ConfigInfo;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLObjectDesharer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.PropertyResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.apache.coyote.http11.Constants.a;

/**
 * @ClassName Demo
 * @Description
 * @Author wangjie
 * @Date 2020/7/4 3:11 下午
 * @Email wangjie_fourth@163.com
 **/
@Slf4j
@Component
public class Demo {

    @Autowired
    private ConfigInfo configInfo;

    public static final String base = "http://www.semanticweb.org/administrator/ontologies/2020/5/untitled-ontology-9";
    public static final PrefixManager pm = new DefaultPrefixManager(null, null, base);



    public boolean hasCLass(String className) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = load(manager);
        System.out.println("================" + "获取所有类" + "==================");
        Set<OWLClass> classesInSignature = ontology.getClassesInSignature();
        for (OWLClass item : classesInSignature) {
            String sourceClassName = item.getIRI().getFragment();
            if (sourceClassName.equals(className)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasOWLNamedIndividual(String owlNamedIndividualName) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = load(manager);
        System.out.println("================" + "获取所有实例" + "==================");
        Set<OWLNamedIndividual> individualsInSignature = ontology.getIndividualsInSignature();
        for (OWLNamedIndividual item : individualsInSignature) {
            String sourceIndividualName = item.getIRI().getFragment();
            log.info("当前实例为：{}", sourceIndividualName);
            if (owlNamedIndividualName.equals(sourceIndividualName)) {
                return true;
            }
        }
        log.info("未找到对应实例");
        return false;
    }

    public boolean hasObjectProperty(String objectPropertyName) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = load(manager);
        System.out.println("================" + "获取所有关系" + "==================");
        Set<OWLObjectProperty> objectPropertiesInSignature = ontology.getObjectPropertiesInSignature();
        System.out.println(objectPropertiesInSignature.size());
        for (OWLObjectProperty item : objectPropertiesInSignature) {
            String sourceObjectProperty = item.getIRI().getFragment();
            if (objectPropertyName.equals(sourceObjectProperty)) {
                return true;
            }
        }
        return false;
    }

    public void createOWLNamedIndividual(String className, String owlNamedIndividualName) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLOntology ontology = load(manager);
        if (!hasCLass(className)) {
            throw new RuntimeException("本体中不含有" + className);
        }
        OWLClass person = df.getOWLClass("#" + className, pm);
        OWLNamedIndividual owlNamedIndividual = df.getOWLNamedIndividual("#" + owlNamedIndividualName, pm);
        OWLClassAssertionAxiom classAssertion = df.getOWLClassAssertionAxiom(person, owlNamedIndividual);
        manager.addAxiom(ontology, classAssertion);

        File outFile = new File(configInfo.filePath);
        IRI outputIRI = IRI.create(outFile);
        try {
            log.info("保存owl文件");
            manager.saveOntology(ontology, outputIRI);
        } catch (OWLOntologyStorageException e) {
            throw new RuntimeException("向本地保存实例出错");
        }
    }

    public void createObjectPropertyForIndividual(String owlNameIndividualName1, String owlNameIndividualName2, String objectProperty) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLOntology ontology = load(manager);
        log.info("开始创建实例之间的关系");
        if (!hasOWLNamedIndividual(owlNameIndividualName1)) {
            throw new RuntimeException("本体中不存在" + owlNameIndividualName1 + "实例");
        }
        if (!hasOWLNamedIndividual(owlNameIndividualName2)) {
            throw new RuntimeException("本体中不存在" + owlNameIndividualName2 + "实例");
        }
        if (!hasObjectProperty(objectProperty)) {
            throw new RuntimeException("本体中不存在" + objectProperty + "关系");
        }

        OWLNamedIndividual owlNamedIndividual1 = df.getOWLNamedIndividual("#" + owlNameIndividualName1, pm);
        OWLNamedIndividual owlNamedIndividual2 = df.getOWLNamedIndividual("#" + owlNameIndividualName2, pm);
        OWLObjectProperty owlObjectProperty = df.getOWLObjectProperty("#" + objectProperty, pm);
        OWLObjectPropertyAssertionAxiom propertyAssertion = df.getOWLObjectPropertyAssertionAxiom(owlObjectProperty,
                owlNamedIndividual1, owlNamedIndividual2);
        manager.addAxiom(ontology, propertyAssertion);

        File outFile = new File(configInfo.filePath);
        IRI outputIRI = IRI.create(outFile);
        try {
            System.out.println("保存数据");
            manager.saveOntology(ontology, outputIRI);
        } catch (OWLOntologyStorageException e) {
            throw new RuntimeException("向本地保存实例出错");
        }
    }

    public void deleteOWLNamedIndividual(String owlNamedIndividual) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLOntology ontology = load(manager);
        if (!hasOWLNamedIndividual(owlNamedIndividual)){
            throw new RuntimeException("本体中不含有" + owlNamedIndividual +"实例");
        }

        OWLEntityRemover remover = new OWLEntityRemover(singleton(ontology));
        OWLNamedIndividual deleteIndividual = df.getOWLNamedIndividual("#" + owlNamedIndividual, pm);
        deleteIndividual.accept(remover);
        for (RemoveAxiom item : remover.getChanges()) {
            manager.applyChange(item);
        }
        File outFile = new File(configInfo.filePath);
        IRI outputIRI = IRI.create(outFile);
        try {
            manager.saveOntology(ontology, outputIRI);
        } catch (OWLOntologyStorageException e) {
            throw new RuntimeException("向本地保存实例出错");
        }
    }

    public void deleteObjectProperty(String owlNameIndividualName1, String owlNameIndividualName2, String objectProperty){
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLOntology ontology = load(manager);
        if (!hasOWLNamedIndividual(owlNameIndividualName1)) {
            throw new RuntimeException("本体中不存在" + owlNameIndividualName1 + "实例");
        }
        if (!hasOWLNamedIndividual(owlNameIndividualName2)) {
            throw new RuntimeException("本体中不存在" + owlNameIndividualName2 + "实例");
        }
        if (!hasObjectProperty(objectProperty)) {
            throw new RuntimeException("本体中不存在" + objectProperty + "关系");
        }

        OWLNamedIndividual owlNamedIndividual1 = df.getOWLNamedIndividual("#" + owlNameIndividualName1, pm);
        OWLNamedIndividual owlNamedIndividual2 = df.getOWLNamedIndividual("#" + owlNameIndividualName2, pm);
        OWLObjectProperty owlObjectProperty = df.getOWLObjectProperty("#" + objectProperty, pm);
        OWLObjectPropertyAssertionAxiom propertyAssertion = df.getOWLObjectPropertyAssertionAxiom(owlObjectProperty,
                owlNamedIndividual1, owlNamedIndividual2);

        manager.removeAxiom(ontology, propertyAssertion);

        File outFile = null;
        try {
            outFile = ResourceUtils.getFile(configInfo.filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        IRI outputIRI = IRI.create(outFile);
        try {
            System.out.println("保存数据");
            manager.saveOntology(ontology, outputIRI);
        } catch (OWLOntologyStorageException e) {
            throw new RuntimeException("向本地保存实例出错");
        }
    }

    public OWLOntology load(OWLOntologyManager manager) {
        StringBuilder content = new StringBuilder();
        File outFile = new File(configInfo.filePath);
        try {
            List<String> lines = Files.readAllLines(outFile.toPath(), StandardCharsets.UTF_8);
            lines.forEach(content::append);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            return manager.loadOntologyFromOntologyDocument(new StringDocumentSource(content.toString()));
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }
}
