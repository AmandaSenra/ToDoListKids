package com.example.todolistkids

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.todolistkids.databinding.FragmentNovaPlanilhaTarefasBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Fragment1_NovaPlanilhaTarefas(var taskItem: TaskItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNovaPlanilhaTarefasBinding
    private lateinit var modeloTarefas: ModeloTarefas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if (taskItem != null){
            binding.taskTitle.text = "Editar Tarefa"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.nome)
            binding.desc.text = editable.newEditable(taskItem!!.descricao)
        }
        else{
            binding.taskTitle.text = "Nova Tarefa"
        }
        modeloTarefas = ViewModelProvider(activity).get(ModeloTarefas::class.java)
        binding.saveButton.setOnClickListener {
            saveAction()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNovaPlanilhaTarefasBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun saveAction() {
        val nome = binding.name.text.toString()
        val descricao = binding.desc.text.toString()
        if(taskItem == null){
            val novaTarefa = TaskItem(nome,descricao,null,null)
            modeloTarefas.addTaskItem(novaTarefa)
        }
        else{
            modeloTarefas.updateTaskItem(taskItem!!.id,nome,descricao,null)
        }
        binding.name.setText("")
        binding.desc.setText("")
        dismiss()
    }

}