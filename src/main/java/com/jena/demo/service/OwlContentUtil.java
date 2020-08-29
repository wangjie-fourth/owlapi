package com.jena.demo.service;

import com.jena.demo.DemoApplication;
import com.jena.demo.config.ConfigInfo;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;


/**
 * @ClassName Demo
 * @Description
 * @Author wangjie
 * @Date 2020/7/4 3:11 下午
 * @Email wangjie_fourth@163.com
 **/
@Slf4j
@Component
public class OwlContentUtil {

    @Resource
    private ConfigInfo configInfo;

    public String getBase() {
        // 从缓存对象中读取数据
        if (Objects.nonNull(DemoApplication.globalOwlContent.getBaseUrl())) {
            log.info("read base from memory");
            DemoApplication.read.lock();
            try {
                return DemoApplication.globalOwlContent.getBaseUrl();
            } finally {
                DemoApplication.read.unlock();
            }
        }

        // 从文件中读取
        log.info("read base from file");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = load(manager);
        if (!ontology.getOntologyID().getOntologyIRI().isPresent()) {
            log.error("找不到base信息");
            throw new RuntimeException("找不到对应的base信息");
        }
        return ontology.getOntologyID().getOntologyIRI().get().toString();
    }

    public boolean hasCLass(String className) {
        // 从缓存对象中读取数据
        if (Objects.nonNull(DemoApplication.globalOwlContent.getClassNameList())) {
            log.info("read class from memory");
            DemoApplication.read.lock();
            try {
                return DemoApplication.globalOwlContent.getClassNameList().stream().anyMatch(i -> i.equals(className));
            } finally {
                DemoApplication.read.unlock();
            }
        }

        // 从文件中读取数据
        log.info("read class from file");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = load(manager);
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
        // 从内存读取数据
        if (Objects.nonNull(DemoApplication.globalOwlContent.getNamedIndividualList())) {
            log.info("read individual from memory");
            DemoApplication.read.lock();
            try {
                return DemoApplication.globalOwlContent.getNamedIndividualList().stream().anyMatch(i -> {
                    String targetName = getBase() + "#" + owlNamedIndividualName;
                    return targetName.equals(i);
                });
            } finally {
                DemoApplication.read.unlock();
            }
        }

        // 从文件读取数据
        log.info("read individual from file");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = load(manager);
        Set<OWLNamedIndividual> individualsInSignature = ontology.getIndividualsInSignature();
        for (OWLNamedIndividual item : individualsInSignature) {
            String fullName = item.getIRI().getNamespace() + item.getIRI().getFragment();
            String targetName = getBase() + "#" + owlNamedIndividualName;
            if (targetName.equals(fullName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasObjectProperty(String objectPropertyName) {
        // 从内存读取数据
        if (Objects.nonNull(DemoApplication.globalOwlContent.getObjectPropertyList())) {
            log.info("read objectProperty from memory");
            DemoApplication.read.lock();
            try {
                return DemoApplication.globalOwlContent.getObjectPropertyList().stream().anyMatch(i -> objectPropertyName.equals(i));
            } finally {
                DemoApplication.read.unlock();
            }
        }

        // 从文件读取数据
        log.info("read objectProperty from file");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = load(manager);
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
        log.info("start write individual ...");
        DemoApplication.write.lock();
        try {
            PrefixManager pm = new DefaultPrefixManager(null, null, getBase());
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
        } finally {
            log.info("end write individual ...");
            initGlobalOwlContent();
            DemoApplication.write.unlock();
        }
    }

    public void createObjectPropertyForIndividual(String owlNameIndividualName1, String owlNameIndividualName2, String objectProperty) {
        log.info("start write object property ...");
        DemoApplication.write.lock();
        try {
            PrefixManager pm = new DefaultPrefixManager(null, null, getBase());
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory df = manager.getOWLDataFactory();
            OWLOntology ontology = load(manager);
            log.info("开始创建实例之间的关系");
            if (!hasOWLNamedIndividual(owlNameIndividualName1)) {
                throw new RuntimeException("本体中不存在【" + owlNameIndividualName1 + "】实例");
            }
            if (!hasOWLNamedIndividual(owlNameIndividualName2)) {
                throw new RuntimeException("本体中不存在【" + owlNameIndividualName2 + "】实例");
            }
            if (!hasObjectProperty(objectProperty)) {
                throw new RuntimeException("本体中不存在【" + objectProperty + "】关系");
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
        } finally {
            log.info("end write object property ...");
            initGlobalOwlContent();
            DemoApplication.write.unlock();
        }
    }

    public void deleteOWLNamedIndividual(String owlNamedIndividual) {
        log.info("start delete individual ...");
        DemoApplication.write.lock();
        try {
            PrefixManager pm = new DefaultPrefixManager(null, null, getBase());
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory df = manager.getOWLDataFactory();
            OWLOntology ontology = load(manager);
            if (!hasOWLNamedIndividual(owlNamedIndividual)) {
                throw new RuntimeException("本体中不含有【" + owlNamedIndividual + "】实例");
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
        } finally {
            log.info("end delete individual ...");
            initGlobalOwlContent();
            DemoApplication.write.unlock();
        }
    }

    public void deleteObjectProperty(String owlNameIndividualName1, String owlNameIndividualName2, String objectProperty) {
        log.info("start delete object property ...");
        DemoApplication.write.lock();
        try {
            PrefixManager pm = new DefaultPrefixManager(null, null, getBase());
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
        } finally {
            log.info("end delete object property ...");
            initGlobalOwlContent();
            DemoApplication.write.unlock();
        }
    }

    public OWLOntology load(OWLOntologyManager manager) {
        log.info("start load ...");
        DemoApplication.read.lock();
        try {
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
        } finally {
            log.info("end load ...");
            DemoApplication.read.unlock();
        }
    }

    public void initGlobalOwlContent() {
        log.info("start init ...");
        // 获取文件数据
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology owlOntology = load(manager);
        String baseUrl = getBase();
        List<String> classList = initClass(owlOntology);
        List<String> namedIndividualList = initNamedIndividualList(owlOntology);
        List<String> objectPropertyList = initObjectPropertyList(owlOntology);

        // 向缓存对象注入数据
        DemoApplication.globalOwlContent.setBaseUrl(baseUrl);
        DemoApplication.globalOwlContent.setClassNameList(classList);
        DemoApplication.globalOwlContent.setNamedIndividualList(namedIndividualList);
        DemoApplication.globalOwlContent.setObjectPropertyList(objectPropertyList);

        log.info("end init");
    }

    private List<String> initObjectPropertyList(OWLOntology owlOntology) {
        return owlOntology.getObjectPropertiesInSignature().stream().map(item -> item.getIRI().getFragment()).collect(Collectors.toList());
    }

    private List<String> initNamedIndividualList(OWLOntology owlOntology) {
        return owlOntology.getIndividualsInSignature().stream().map(item -> item.getIRI().getNamespace() + item.getIRI().getFragment()).collect(Collectors.toList());
    }

    private List<String> initClass(OWLOntology owlOntology) {
        Set<OWLClass> classesInSignature = owlOntology.getClassesInSignature();
        return classesInSignature.stream().map(item -> item.getIRI().getFragment()).collect(Collectors.toList());
    }
}
